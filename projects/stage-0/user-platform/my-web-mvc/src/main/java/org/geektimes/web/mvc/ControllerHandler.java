package org.geektimes.web.mvc;

import lombok.extern.slf4j.Slf4j;
import org.geektimes.web.core.BeanContainer;
import org.geektimes.web.mvc.annotation.RequestMapping;
import org.geektimes.web.mvc.annotation.RequestMethod;
import org.geektimes.web.mvc.annotation.RequestParam;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
public class ControllerHandler {

    /**
     * 存放请求信息与控制器的映射关系
     **/
    private Map<PathInfo, ControllerInfo> pathControllerMap = new ConcurrentHashMap<>();

    /**
     * Bean 容器
     **/
    private BeanContainer beanContainer;

    public ControllerHandler() {
        beanContainer = BeanContainer.getInstance();
        Set<Class<?>> classSet = beanContainer.getClassesByAnnotation(RequestMapping.class);
        for (Class<?> clz : classSet) {
            putPathController(clz);
        }
    }

    /**
     * 获取ControllerInfo
     */
    public ControllerInfo getController(String requestMethod, String requestPath) {
        PathInfo pathInfo = new PathInfo(requestMethod, requestPath);
        return pathControllerMap.get(pathInfo);
    }

    /**
     * 添加信息到requestControllerMap中
     */
    private void putPathController(Class<?> clz) {
        // 获取控制器的模块路径，此处没有对 controllerRequest 进行控制，有NPE可能性
        RequestMapping controllerRequest = clz.getAnnotation(RequestMapping.class);
        String basePath = controllerRequest.value();
        // 获取类自身声明的所有方法
        Method[] controllerMethods = clz.getDeclaredMethods();
        // 1. 遍历Controller中的方法
        for (Method method : controllerMethods) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                // 2. 获取这个方法的参数名字和参数类型
                Map<String, Class<?>> params = new HashMap<>();
                for (Parameter methodParam : method.getParameters()) {
                    RequestParam requestParam = methodParam.getAnnotation(RequestParam.class);
                    if (null == requestParam) {
                        throw new RuntimeException("必须有RequestParam指定的参数名");
                    }
                    params.put(requestParam.value(), methodParam.getType());
                }
                // 3. 获取这个方法上的RequestMapping注解， 存在NPE可能
                RequestMapping methodRequest = method.getAnnotation(RequestMapping.class);
                String methodPath = methodRequest.value();
                RequestMethod requestMethod = methodRequest.method();
                PathInfo pathInfo = new PathInfo(requestMethod.toString(), basePath + methodPath);
                if (pathControllerMap.containsKey(pathInfo)) {
                    log.error("url:{} 重复注册", pathInfo.getHttpPath());
                    throw new RuntimeException("url重复注册");
                }
                // 4. 生成ControllerInfo并存入Map中
                ControllerInfo controllerInfo = new ControllerInfo(clz, method, params);
                this.pathControllerMap.put(pathInfo, controllerInfo);
                log.info("Add Controller RequestMethod:{}, RequestPath:{}, Controller:{}, Method:{}",
                        pathInfo.getHttpMethod(), pathInfo.getHttpPath(),
                        controllerInfo.getControllerClass().getName(), controllerInfo.getInvokeMethod().getName());
            }
        }
    }
}
