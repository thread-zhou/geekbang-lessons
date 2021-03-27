package org.geektimes.web.core.context.provider;

import org.geektimes.web.core.AbstractComponentContext;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @ClassName: AbstractComponentContextProvider
 * @Description: {@link ComponentContextProvider}公共实现
 * @author: zhoujian
 * @date: 2021/3/11 13:37
 * @version: 1.0
 */
public abstract class AbstractComponentContextProvider extends AbstractComponentContext implements ComponentContextProvider {

    private Map<String, Object> additionalComponentContextCache = new LinkedHashMap<>();

    @Override
    public void provide(ServletContext servletContext) throws RuntimeException {
        preInit(servletContext);
        initEnvContext();
        // 实例化组件
        loadComponents();
        if (additionalComponentContextCache.size() > 0){
            refresh(Collections.unmodifiableMap(additionalComponentContextCache));
            getLogger().info("[" + getClass().getSimpleName()
                    + "] 已完成组件上下文加载，并将其刷新到全局组件上下文，当前共注入组件: ["
                    + additionalComponentContextCache.size() + "]，即将进行初始化");
            // 初始化组件
            initializeComponents();
        }
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
        additionalComponentContextCache.values().forEach(this::initializeComponent);
    }

    /**
     * 追加附加组件
     * @author zhoujian
     * @date 11:41 2021/3/27
     * @param name 附加组件名称
     * @param component 附加组件对象
     * @return void
     **/
    protected void appendAdditionalComponent(String name, Object component){
        additionalComponentContextCache.put(name, component);
    }

}
