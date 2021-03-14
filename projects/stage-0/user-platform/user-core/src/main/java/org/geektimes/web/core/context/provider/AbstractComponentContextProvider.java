package org.geektimes.web.core.context.provider;

import org.geektimes.web.core.AbstractComponentContext;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.logging.Logger;

/**
 * @ClassName: AbstractComponentContextProvider
 * @Description: {@link ComponentContextProvider}公共实现
 * @author: zhoujian
 * @date: 2021/3/11 13:37
 * @version: 1.0
 */
public abstract class AbstractComponentContextProvider extends AbstractComponentContext implements ComponentContextProvider {

    private static final Logger logger = Logger.getLogger(ComponentContextProvider.class.getName());

    @Override
    public void provide(ServletContext servletContext) throws RuntimeException {
        preInit(servletContext);
        initEnvContext();
        loadComponents();
        initializeComponents();
    }

    /**
     * 初始化前准备
     * @author zhoujian
     * @date 13:43 2021/3/11
     * @param servletContext
     * @return void
     **/
    protected void preInit(ServletContext servletContext) throws RuntimeException{}

    protected abstract void initEnvContext() throws RuntimeException;

    /**
     * 实例化组件
     * @author zhoujian
     * @date 22:34 2021/3/10
     * @param
     * @return void
     **/
    protected abstract void loadComponents() throws RuntimeException;

    protected Logger getLogger(){
        return logger;
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
    protected void initializeComponents() throws RuntimeException {
        getComponentsMap().values().forEach(component -> {
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
    protected abstract void injectComponents(Object component, Class<?> componentClass);

    /**
     * 用于支持 {@link PostConstruct}
     * @author zhoujian
     * @date 22:52 2021/3/10
     * @param component
     * @param componentClass
     * @return void
     **/
    protected abstract void processPostConstruct(Object component, Class<?> componentClass);

    /**
     * 用于支持 {@link PreDestroy}
     * @author zhoujian
     * @date 22:52 2021/3/10
     * @param component
     * @param componentClass
     * @return void
     **/
    protected abstract void processPreDestroy(Object component, Class<?> componentClass);

}
