package org.geektimes.web.core.context.provider;

import org.geektimes.web.FuYi;
import org.geektimes.web.function.ThrowableAction;
import org.geektimes.web.function.ThrowableFunction;
import javax.naming.*;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName: JndiComponentContextProvider
 * @Description: JNDI 组件上下文提供器
 * @author: zhoujian
 * @date: 2021/3/11 13:07
 * @version: 1.0
 */
public class JndiComponentContextProvider extends AbstractComponentContextProvider implements ComponentContextProvider{

    private static final String COMPONENT_ENV_CONTEXT_NAME = "java:comp/env";

    private boolean naming = false;

    // Component Env Context
    private Context envContext;

    private ClassLoader classLoader;

    @Override
    protected void preInit(ServletContext servletContext) throws RuntimeException {
        this.naming = FuYi.getConfiguration().isNaming();
        // 获取当前 ServletContext（WebApp）ClassLoader
        this.classLoader = servletContext.getClassLoader();
    }

    @Override
    protected void loadComponents() throws RuntimeException {
        // 遍历获取所有的组件名称
        List<String> componentNames = listAllComponentNames();
        // 通过依赖查找，实例化对象（ Tomcat BeanFactory setter 方法的执行，仅支持简单类型）
        componentNames.forEach(name -> appendAdditionalComponent(name, lookupComponent(name)));
    }

    private List<String> listAllComponentNames() {
        return listComponentNames("/");
    }

    private static void close(Context context) {
        if (context != null) {
            ThrowableAction.execute(context::close);
        }
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
                getLogger().warning(e.getMessage());
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
    protected void initEnvContext() throws RuntimeException {
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

    /**
     * JNDI上下文初始化时 组件查找方法
     * @author zhoujian
     * @date 11:34 2021/3/27
     * @param name
     * @return C
     **/
    protected  <C> C lookupComponent(String name) {
        return executeInContext(context -> (C) context.lookup(name));
    }

}
