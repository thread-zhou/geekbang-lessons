package org.geektimes.web.mvc.handler;

import lombok.extern.slf4j.Slf4j;
import org.geektimes.web.core.BeanContainer;
import org.geektimes.web.mvc.ControllerInfo;
import org.geektimes.web.mvc.PathInfo;
import org.geektimes.web.mvc.annotation.RequestMapping;
import org.geektimes.web.mvc.annotation.RequestMethod;
import org.geektimes.web.mvc.annotation.RequestParam;
import org.geektimes.web.mvc.annotation.ResponseBody;
import org.geektimes.web.mvc.render.JsonRender;
import org.geektimes.web.mvc.render.NotFoundRender;
import org.geektimes.web.mvc.render.Render;
import org.geektimes.web.mvc.render.ViewRender;
import org.geektimes.web.util.CastUtil;
import org.geektimes.web.util.ValidateUtil;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @ClassName: ControllerHandler
 * @Description: Controller分发器类
 *
 * <p>
 * 这个类最复杂的就是构造函数中调用的putPathController()方法，这个方法也是这个类的核心方法，
 * 实现了controller类中的信息存放到pathControllerMap变量中的功能。大概讲解一些这个类的功能流程：
 *
 * 1、在构造方法中获取Bean容器BeanContainer的单例实例
 * 2、获取并遍历BeanContainer中存放的被RequestMapping注解标记的类
 * 3、遍历这个类中的方法，找出被RequestMapping注解标记的方法
 * 4、获取这个方法的参数名字和参数类型，生成ControllerInfo
 * 5、根据RequestMapping里的value()和method()生成PathInfo
 * 6、将生成的PathInfo和ControllerInfo存到变量pathControllerMap中
 * 7、其他类通过调用getController()方法获取到对应的controller
 *
 *
 * 以上就是这个类的流程，其中有个注意的点：
 *
 * 步骤4的时候，必须规定这个方法的所有参数名字都被RequestParam注解标注，
 * 这是因为在java中，虽然我们编写代码的时候是有参数名的，比如String name这样的形式，
 * 但是被编译成class文件后‘name’这个字段就会被擦除，所以必须要通过一个RequestParam来保存名字。
 *
 * 但是大家在springmvc中并不用必须每个方法都用注解标记的，这是因为spring中借助了*asm* ，
 * 这种工具可以在编译之前拿到参数名然后保存起来。还有一种方法是在java8之后支持了保存参数名，
 * 但是必须修改编译器的参数来支持。这两种方法实现起来都比较复杂或者有限制条件，这里就不实现了，大家可以查找资料自己实现
 * </p>
 *
 * @author: zhoujian
 * @date: 2021/3/4 17:43
 * @version: 1.0
 */
@Slf4j
public class ControllerHandler implements Handler{

    /**
     * 请求信息和controller信息关系map
     */
    private Map<PathInfo, ControllerInfo> pathControllerMap = new ConcurrentHashMap<>();
    /**
     * bean容器
     */
    private BeanContainer beanContainer;

    public ControllerHandler() {
        beanContainer = BeanContainer.getInstance();

        Set<Class<?>> mappingSet = beanContainer.getClassesByAnnotation(RequestMapping.class);
        this.initPathControllerMap(mappingSet);
    }

    @Override
    public boolean handle(final RequestHandlerChain handlerChain) throws Exception {
        String method = handlerChain.getRequestMethod();
        String path = handlerChain.getRequestPath();
        ControllerInfo controllerInfo = pathControllerMap.get(new PathInfo(method, path));
        if (null == controllerInfo) {
            handlerChain.setRender(new NotFoundRender());
            return false;
        }
        Object result = invokeController(controllerInfo, handlerChain.getRequest());
        setRender(result, controllerInfo, handlerChain);
        return true;
    }

    /**
     * 执行controller方法
     */
    private Object invokeController(ControllerInfo controllerInfo, HttpServletRequest request) {
        Map<String, String> requestParams = getRequestParams(request);
        List<Object> methodParams = instantiateMethodArgs(controllerInfo.getMethodParameter(), requestParams);

        Object controller = beanContainer.getBean(controllerInfo.getControllerClass());
        Method invokeMethod = controllerInfo.getInvokeMethod();
        invokeMethod.setAccessible(true);
        Object result;
        try {
            if (methodParams.size() == 0) {
                result = invokeMethod.invoke(controller);
            } else {
                result = invokeMethod.invoke(controller, methodParams.toArray());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

    /**
     * 设置请求结果执行器
     */
    private void setRender(Object result, ControllerInfo controllerInfo, RequestHandlerChain handlerChain) {
        if (null == result) {
            return;
        }
        Render render;
        boolean isJson = controllerInfo.getInvokeMethod().isAnnotationPresent(ResponseBody.class);
        if (isJson) {
            render = new JsonRender(result);
        } else {
            render = new ViewRender(result);
        }
        handlerChain.setRender(render);
    }

    /**
     * 初始化pathControllerMap
     */
    private void initPathControllerMap(Set<Class<?>> mappingSet) {
        mappingSet.forEach(this::addPathController);
    }

    /**
     * 添加controllerInfo到pathControllerMap中
     */
    private void addPathController(Class<?> clz) {
        RequestMapping requestMapping = clz.getAnnotation(RequestMapping.class);
        String basePath = requestMapping.value();
        if (!basePath.startsWith("/")) {
            basePath = "/" + basePath;
        }
        for (Method method : clz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping methodRequest = method.getAnnotation(RequestMapping.class);
                String methodPath = methodRequest.value();
                if (!methodPath.startsWith("/")) {
                    methodPath = "/" + methodPath;
                }
                String url = basePath + methodPath;
                Map<String, Class<?>> methodParams = this.getMethodParams(method);
                String httpMethod = String.valueOf(methodRequest.method());
                PathInfo pathInfo = new PathInfo(httpMethod, url);
                if (pathControllerMap.containsKey(pathInfo)) {
                    log.warn("url:{} 重复注册", pathInfo.getHttpPath());
                }
                ControllerInfo controllerInfo = new ControllerInfo(clz, method, methodParams);
                this.pathControllerMap.put(pathInfo, controllerInfo);
                log.info("mapped:[{},method=[{}]] controller:[{}@{}]",
                        pathInfo.getHttpPath(), pathInfo.getHttpMethod(),
                        controllerInfo.getControllerClass().getName(), controllerInfo.getInvokeMethod().getName());
            }
        }
    }

    /**
     * 获取执行方法的参数
     */
    private Map<String, Class<?>> getMethodParams(Method method) {
        Map<String, Class<?>> map = new HashMap<>();
        for (Parameter parameter : method.getParameters()) {
            RequestParam param = parameter.getAnnotation(RequestParam.class);
            // TODO: 不使用注解匹配参数名字
            if (null == param) {
                throw new RuntimeException("必须有RequestParam指定的参数名");
            }
            map.put(param.value(), parameter.getType());
        }
        return map;
    }

    /**
     * 获取HttpServletRequest中的参数
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
}
