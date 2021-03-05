package org.geektimes.web.mvc;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.geektimes.web.FuYi;
import org.geektimes.web.core.BeanContainer;
import org.geektimes.web.mvc.annotation.ResponseBody;
import org.geektimes.web.mvc.bean.ModelAndView;
import org.geektimes.web.util.CastUtil;
import org.geektimes.web.util.ValidateUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ResultRender
 * @Description: 结果执行器
 *
 * <p>
 * 通过调用类中的invokeController()方法反射调用了Controller中的方法并根据结果解析对应的页面。主要流程为：
 *
 * 1、调用getRequestParams() 获取HttpServletRequest中参数
 * 2、调用instantiateMethodArgs() 实例化调用方法要传入的参数值
 * 3、通过反射调用目标controller的目标方法
 * 4、调用resultResolver()解析方法的返回值，选择返回页面或者json
 * 通过这几个步骤算是凝聚了MVC核心步骤了，不过由于篇幅问题，几乎每一步骤得功能都有所精简，如
 *
 * 步骤1获取HttpServletRequest中参数只获取get或者post传的参数，实际上还有 Body、Path、Header等方式的请求参数获取没有实现
 * 步骤2实例化调用方法的值只实现了java的原生参数，自定义的类的实例化没有实现
 * 步骤4异常统一处理也没具体实现
 * 虽然有缺陷，但是一个MVC流程是完成了。接下来就要把这些功能组装一下了。
 * </p>
 * @author: zhoujian
 * @date: 2021/3/4 17:58
 * @version: 1.0
 */
@Slf4j
public class ResultRender {

    private BeanContainer beanContainer;

    public ResultRender() {
        beanContainer = BeanContainer.getInstance();
    }

    /**
     * 执行Controller的方法
     */
    public void invokeController(HttpServletRequest req, HttpServletResponse resp, ControllerInfo controllerInfo) {
        // 1. 获取HttpServletRequest所有参数
        Map<String, String> requestParam = getRequestParams(req);
        // 2. 实例化调用方法要传入的参数值
        List<Object> methodParams = instantiateMethodArgs(controllerInfo.getMethodParameter(), requestParam);

        Object controller = beanContainer.getBean(controllerInfo.getControllerClass());
        Method invokeMethod = controllerInfo.getInvokeMethod();
        // 取消 Java 语言访问检查，实际上setAccessible是启用和禁用访问安全检查的开关,并不是为true就能访问为false就不能访问（这里仅指代方法，属性是私有则必须设置setAccessible(true);）
        invokeMethod.setAccessible(true);
        Object result;
        // 3. 通过反射调用方法
        try {
            if (methodParams.size() == 0) {
                result = invokeMethod.invoke(controller);
            } else {
                result = invokeMethod.invoke(controller, methodParams.toArray());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 4.解析方法的返回值，选择返回页面或者json
        resultResolver(controllerInfo, result, req, resp);
    }

    /**
     * 获取http中的参数
     */
    private Map<String, String> getRequestParams(HttpServletRequest request) {
        Map<String, String> paramMap = new HashMap<>();
        //GET和POST方法是这样获取请求参数的
        request.getParameterMap().forEach((paramName, paramsValues) -> {
            if (ValidateUtil.isNotEmpty(paramsValues)) {
                paramMap.put(paramName, paramsValues[0]);
            }
        });
        // TODO: Body、Path、Header等方式的请求参数获取
        return paramMap;
    }

    /**
     * 实例化方法参数
     */
    private List<Object> instantiateMethodArgs(Map<String, Class<?>> methodParams, Map<String, String> requestParams) {
        return methodParams.keySet().stream().map(paramName -> {
            Class<?> type = methodParams.get(paramName);
            String requestValue = requestParams.get(paramName);
            Object value;
            if (null == requestValue) {
                value = CastUtil.primitiveNull(type);
            } else {
                value = CastUtil.convert(type, requestValue);
                // TODO: 实现非原生类的参数实例化
            }
            return value;
        }).collect(Collectors.toList());
    }


    /**
     * Controller方法执行后返回值解析
     */
    private void resultResolver(ControllerInfo controllerInfo, Object result, HttpServletRequest req, HttpServletResponse resp) {
        if (null == result) {
            return;
        }
        boolean isJson = controllerInfo.getInvokeMethod().isAnnotationPresent(ResponseBody.class);
        if (isJson) {
            // 设置响应头
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            // 向响应中写入数据
            try (PrintWriter writer = resp.getWriter()) {
                writer.write(JSON.toJSONString(result));
                writer.flush();
            } catch (IOException e) {
                log.error("转发请求失败", e);
                // TODO: 异常统一处理，400等...
            }
        } else {
            String path;
            if (result instanceof ModelAndView) {
                ModelAndView mv = (ModelAndView) result;
                path = mv.getView();
                Map<String, Object> model = mv.getModel();
                if (ValidateUtil.isNotEmpty(model)) {
                    for (Map.Entry<String, Object> entry : model.entrySet()) {
                        req.setAttribute(entry.getKey(), entry.getValue());
                    }
                }
            } else if (result instanceof String) {
                path = (String) result;
            } else {
                throw new RuntimeException("返回类型不合法");
            }
            try {
//                req.getRequestDispatcher("/templates/" + path).forward(req, resp);
                req.getRequestDispatcher(FuYi.getConfiguration().getResourcePath() + path).forward(req, resp);
            } catch (Exception e) {
                log.error("转发请求失败", e);
                // TODO: 异常统一处理，400等...
            }
        }
    }
}
