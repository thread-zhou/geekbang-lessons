package org.geektimes.web.core;
import org.geektimes.web.core.context.provider.ComponentContextProvider;
import org.geektimes.web.function.ThrowableAction;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName: AbstractComponentContext
 * @Description: {@link ComponentContext} 基础实现
 * @author: zhoujian
 * @date: 2021/3/9 21:24
 * @version: 1.0
 */
public abstract class AbstractComponentContext implements ComponentContext {

    private ServletContext servletContext;

    /**
     * 组件缓存
     **/
    private static Map<String, Object> COMPONENT_CACHE = new LinkedHashMap<>();

    /**
     * @PreDestroy 方法缓存，Key 为标注方法，Value 为方法所属对象
     */
    private Map<Method, Object> preDestroyMethodCache = new LinkedHashMap<>();

    private static final Logger logger = Logger.getLogger(ComponentContext.class.getName());

    @Override
    public void init(ServletContext servletContext) throws RuntimeException {
        this.servletContext = servletContext;
        additionalInit(servletContext);
        for (ComponentContextProvider provider : ServiceLoader.load(ComponentContextProvider.class)){
            provider.provide(servletContext);
        }
        registerShutdownHook();
    }

    @Override
    public void refresh(Map<String, Object> additionalComponentContexts) throws RuntimeException {
        // TODO: 2021/3/27 缺少key重复检测
        COMPONENT_CACHE.putAll(additionalComponentContexts);
    }

    @Override
    public void setComponent(String name, Object c) {
        COMPONENT_CACHE.put(name, c);
    }

    protected Logger getLogger(){
        return logger;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public <C> C getComponent(String name) {
        return (C) COMPONENT_CACHE.get(name);
    }

    @Override
    public <C> List<C> getComponentsBySuperClass(Class<C> c) {
        return COMPONENT_CACHE.values().stream()
                .filter(component -> c.isAssignableFrom(component.getClass()))
                .map((component -> (C) component)).collect(Collectors.toList());
    }

    @Override
    public void registerComponent(Object component) throws RuntimeException {
        // TODO: 2021/3/27 缺少key重复检测
        COMPONENT_CACHE.put(component.getClass().getSimpleName(), component);
        initializeComponent(component);
    }

    /**
     * 获取组件类中的候选方法
     *
     * @param componentClass 组件类
     * @return non-null
     */
    private List<Method> findCandidateMethods(Class<?> componentClass) {
        /**
         * 1 method.getModifiers(): 获取访问修饰符：默认情况（什么都不加）: 0  public: 1  private: 2  protected: 4  static: 8  final: 16
         * 多个访问修饰符则为数字和，如：public static final -> 25
         *
         * 2 method.getParameterCount() == 0: 没有参数 --> 规定不给参数，因为无法确定参数，但是可以考虑注入上下文内容，算是留下钩子
         **/
        return Stream.of(componentClass.getMethods())                     // public 方法
                .filter(method ->
                        !Modifier.isStatic(method.getModifiers()) &&      // 非 static
                                method.getParameterCount() == 0)          // 无参数
                .collect(Collectors.toList());
    }

    /**
     * 初始化组件（支持 Java 标准 Commons Annotation 生命周期）
     * <ol>
     *  <li>注入阶段 - {@link Resource}</li>
     *  <li>初始阶段 - {@link PostConstruct}</li>
     *  <li>销毁阶段 - {@link PreDestroy}</li>
     * </ol>
     * @author zhoujian
     * @date 11:52 2021/3/27
     * @param
     * @return void
     **/
    protected void initializeComponent(Object component) {
        Class<?> componentClass = component.getClass();
        // 注入阶段 - {@link Resource}
        injectComponents(component, componentClass);
        /**
         * 查询候选方法
         *
         * 提前过滤，优化不必要的重复动作
         **/
        List<Method> candidateMethods = findCandidateMethods(componentClass);
        // 初始阶段 - {@link PostConstruct}
        processPostConstruct(component, candidateMethods);
        // 实现销毁阶段 - {@link PreDestroy}
        processPreDestroy(component, candidateMethods);
    }


    /**
     * 额外的处理
     * @author zhoujian
     * @date 13:56 2021/3/11
     * @param servletContext
     * @return void
     **/
    protected void additionalInit (ServletContext servletContext) throws RuntimeException {};


    /**
     * 获取所有的组件名称
     *
     * @return
     */
    public List<String> getComponentNames() {
        return new ArrayList<>(COMPONENT_CACHE.keySet());
    }

    @Override
    public void destroy() throws RuntimeException {
    }

    /**
     * 容器内部查找方法, 该方法仅限内部使用
     *
     * 如果需在查找时初始化，或来源不一致，需重写改方法，
     * 参照{@link org.geektimes.web.core.context.provider.JndiComponentContextProvider#lookupComponent(String)}
     *
     * 默认实现为通过全局组件上下文查询
     * @author zhoujian
     * @date 11:25 2021/3/27
     * @param name
     * @return C
     **/
    protected <C> C lookupComponent(String name){
        return getComponent(name);
    }

//    ----------------------------------  组件生命周期方法 Start ------------------------------------------------

    /**
     * 用于支持 {@link Resource}
     * @author zhoujian
     * @date 22:52 2021/3/10
     * @param component
     * @param componentClass
     * @return void
     **/
    protected void injectComponents(Object component, Class<?> componentClass) throws RuntimeException{
        Stream.of(componentClass.getDeclaredFields())
                .filter(field -> {
                    /**
                     * 获取访问修饰符：默认情况（什么都不加）: 0  public: 1  private: 2  protected: 4  static: 8  final: 16
                     *
                     * 多个访问修饰符则为数字和，如：public static final -> 25
                     **/
                    int mods = field.getModifiers();
                    /**
                     * 获取非static修饰的，以及使用{@link Resource}修饰的字段
                     **/
                    return !Modifier.isStatic(mods) &&
                            field.isAnnotationPresent(Resource.class);
                }).forEach(field -> {

            Resource resource = field.getAnnotation(Resource.class);
            String resourceName = resource.name();
            // 依赖查找
            Object injectedObject = lookupComponent(resourceName);
            field.setAccessible(true);
            try {
                // 依赖注入 注入目标对象
                field.set(component, injectedObject);
            } catch (IllegalAccessException e) {
                logger.warning("组件[ "+ componentClass.getName() + " ] 字段 --> [ " + field.getName() + " ] 注入失败");
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 用于支持 {@link PostConstruct}
     * @author zhoujian
     * @date 22:52 2021/3/10
     * @param component
     * @param candidateMethods 候选方法
     * @return void
     **/
    protected void processPostConstruct(Object component, List<Method> candidateMethods) throws RuntimeException{
        candidateMethods
                .stream()
                .filter(method -> method.isAnnotationPresent(PostConstruct.class))// 标注 @PostConstruct
                .forEach(method -> {
                    // 执行目标方法
                    ThrowableAction.execute(() -> method.invoke(component));
                });
    }

    /**
     * 用于支持 {@link PreDestroy}
     * @author zhoujian
     * @date 22:52 2021/3/10
     * @param component
     * @param candidateMethods 候选方法
     * @return void
     **/
    protected void processPreDestroy(Object component, List<Method> candidateMethods) throws RuntimeException {
        candidateMethods.stream()
                .filter(method -> method.isAnnotationPresent(PreDestroy.class)) // 标注 @PreDestroy
                .forEach(method -> {
                    preDestroyMethodCache.put(method, component);
                });
    }

    /**
     * 注册组件销毁钩子
     * @author zhoujian
     * @date 12:03 2021/3/27
     * @param
     * @return void
     **/
    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            processPreDestroy();
        }));
    }

    /**
     * 标注了 {@link PreDestroy} 的组件销毁方法
     * @author zhoujian
     * @date 12:04 2021/3/27
     * @param
     * @return void
     **/
    private void processPreDestroy() {
        for (Method preDestroyMethod : preDestroyMethodCache.keySet()) {
            // 移除集合中的对象，防止重复执行 @PreDestroy 方法
            Object component = preDestroyMethodCache.remove(preDestroyMethod);
            // 执行目标方法
            ThrowableAction.execute(() -> preDestroyMethod.invoke(component));
        }
    }
//    --------------------------------  组件生命周期方法 End ------------------------------------------------
}
