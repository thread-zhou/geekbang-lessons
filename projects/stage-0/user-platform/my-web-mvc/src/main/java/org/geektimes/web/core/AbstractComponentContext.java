package org.geektimes.web.core;
import org.apache.commons.lang.ObjectUtils;
import org.geektimes.web.FuYi;
import org.geektimes.web.function.ThrowableAction;
import org.geektimes.web.function.ThrowableFunction;
import org.geektimes.web.util.CastUtil;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.naming.*;
import javax.servlet.ServletContext;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collector;
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

    private static final Logger logger = Logger.getLogger(COMPONENT_CONTEXT_NAME);

    private static final String COMPONENT_ENV_CONTEXT_NAME = "java:comp/env";
    
    private boolean naming = false;

    // Component Env Context
    private Context envContext;

    private ClassLoader classLoader;

    private Map<String, Object> componentsMap = new LinkedHashMap<>();

    private static void close(Context context) {
        if (context != null) {
            ThrowableAction.execute(context::close);
        }
    }

    @Override
    public void init(ServletContext servletContext) throws RuntimeException {
        this.servletContext = servletContext;
        this.naming = FuYi.getConfiguration().isNaming();
        // 获取当前 ServletContext（WebApp）ClassLoader
        this.classLoader = servletContext.getClassLoader();
        initEnvContext();
        genericInit(servletContext);
        loadComponents();
        initializeComponents();
    }

    private void initEnvContext() throws RuntimeException {
        if (naming){
            if (this.envContext != null) {
                return;
            }
            Context context = null;
            try {
                context = new InitialContext();
                this.envContext = (Context) context.lookup(COMPONENT_ENV_CONTEXT_NAME);
            } catch (NamingException e) {
                throw new RuntimeException(e);
            } finally {
                close(context);
            }
        }
    }

    private List<String> listAllComponentNames() {
        return listComponentNames("/");
    }

    /**
     * 实例化组件
     * @author zhoujian
     * @date 22:34 2021/3/10
     * @param
     * @return void
     **/
    protected void loadComponents() {
        // 遍历获取所有的组件名称
        List<String> componentNames = listAllComponentNames();
        // 通过依赖查找，实例化对象（ Tomcat BeanFactory setter 方法的执行，仅支持简单类型）
        componentNames.forEach(name -> componentsMap.put(name, lookupComponent(name)));
    }

    /**
     * 初始化组件（支持 Java 标准 Commons Annotation 生命周期）
     * <ol>
     *  <li>注入阶段 - {@link Resource}</li>
     *  <li>初始阶段 - {@link PostConstruct}</li>
     *  <li>销毁阶段 - {@link PreDestroy}</li>
     * </ol>
     * @author zhoujian
     * @date 22:35 2021/3/10
     * @param
     * @return void
     **/
    protected void initializeComponents() {
        componentsMap.values().forEach(component -> {
            Class<?> componentClass = component.getClass();
            // 注入阶段 - {@link Resource}
            injectComponents(component, componentClass);
            // 初始阶段 - {@link PostConstruct}
            processPostConstruct(component, componentClass);
            // 实现销毁阶段 - {@link PreDestroy}
            processPreDestroy(component, componentClass);
        });

    }

    /**
     * 用于支持 {@link Resource}
     * @author zhoujian
     * @date 22:52 2021/3/10
     * @param component
     * @param componentClass
     * @return void
     **/
    private void injectComponents(Object component, Class<?> componentClass) {
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
                }
        });
    }

    /**
     * 用于支持 {@link PostConstruct}
     * @author zhoujian
     * @date 22:52 2021/3/10
     * @param component
     * @param componentClass
     * @return void
     **/
    private void processPostConstruct(Object component, Class<?> componentClass) {
        Stream.of(componentClass.getMethods())
                .filter(method ->
                        !Modifier.isStatic(method.getModifiers()) &&      // 非 static
                                /**
                                 * 没有参数 --> 规定不给参数，因为无法确定参数，但是可以考虑注入一些上下文内容，算是留下钩子
                                 **/
                                method.getParameterCount() == 0 &&
                                method.isAnnotationPresent(PostConstruct.class) // 标注 @PostConstruct
                ).forEach(method -> {
            // 执行目标方法
            try {
                method.invoke(component);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 用于支持 {@link PreDestroy}
     * @author zhoujian
     * @date 22:52 2021/3/10
     * @param component
     * @param componentClass
     * @return void
     **/
    private void processPreDestroy(Object component, Class<?> componentClass) {
        Stream.of(componentClass.getMethods())
                .filter(method ->
                !Modifier.isStatic(method.getModifiers())
                        && method.isAnnotationPresent(PreDestroy.class)
                        && method.getParameterCount() == 0)
                .forEach(method -> {
                    try {
                        method.invoke(component);
                    }catch (Exception e){
                        throw new RuntimeException(e);
                    }
                });
    }

    protected  <C> C lookupComponent(String name) {
        return executeInContext(context -> (C) context.lookup(name));
    }

    protected List<String> listComponentNames(String name) {
        return executeInContext(context -> {
            NamingEnumeration<NameClassPair> e = executeInContext(context, ctx -> ctx.list(name), true);

            // 目录 - Context
            // 节点 - Entry
            if (e == null) { // 当前 JNDI 名称下没有子节点
                return Collections.emptyList();
            }

            List<String> fullNames = new LinkedList<>();
            while (e.hasMoreElements()) {
                NameClassPair element = e.nextElement();
                String className = element.getClassName();
                Class<?> targetClass = classLoader.loadClass(className);
                if (Context.class.isAssignableFrom(targetClass)) {
                    // 如果当前名称是目录（Context 实现类）的话，递归查找
                    fullNames.addAll(listComponentNames(element.getName()));
                } else {
                    // 否则，当前名称绑定目标类型的话，添加该名称到集合中
                    String fullName = name.startsWith("/") ?
                            element.getName() : name + "/" + element.getName();
                    fullNames.add(fullName);
                }
            }
            return fullNames;
        });
    }



    private <R> R executeInContext(Context context, ThrowableFunction<Context, R> function,
                                   boolean ignoredException) {
        R result = null;
        try {
            result = ThrowableFunction.execute(context, function);
        } catch (Throwable e) {
            if (ignoredException) {
                logger.warning(e.getMessage());
            } else {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    /**
     * 在 Context 中执行，通过指定 ThrowableFunction 返回计算结果
     *
     * @param function ThrowableFunction
     * @param <R>      返回结果类型
     * @return 返回
     * @see ThrowableFunction#apply(Object)
     */
    protected <R> R executeInContext(ThrowableFunction<Context, R> function) {
        return executeInContext(function, false);
    }

    /**
     * 在 Context 中执行，通过指定 ThrowableFunction 返回计算结果
     *
     * @param function         ThrowableFunction
     * @param ignoredException 是否忽略异常
     * @param <R>              返回结果类型
     * @return 返回
     * @see ThrowableFunction#apply(Object)
     */
    protected <R> R executeInContext(ThrowableFunction<Context, R> function, boolean ignoredException) {
        return executeInContext(this.envContext, function, ignoredException);
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public <C> C getComponent(String name) {
        C component = null;
        try {
            component = (C) this.envContext.lookup(name);
        } catch (NamingException e) {
            throw new NoSuchElementException(name);
        }
        return component;
    }

    /**
     * 获取所有的组件名称
     *
     * @return
     */
    public List<String> getComponentNames() {
        return new ArrayList<>(componentsMap.keySet());
    }

    @Override
    public <C> List<C> getComponentsBySuperClass(Class<C> c) {
        return componentsMap.values().stream()
                .filter(component -> c.isAssignableFrom(component.getClass()))
                .map((component -> (C) component)).collect(Collectors.toList());
    }

    protected Context getEnvContext(){
        return envContext;
    }

    protected void genericInit (ServletContext servletContext) throws RuntimeException {};

    @Override
    public void destroy() throws RuntimeException {
    }
}
