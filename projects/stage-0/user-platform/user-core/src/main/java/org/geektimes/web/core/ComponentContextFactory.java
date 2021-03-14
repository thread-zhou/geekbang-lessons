package org.geektimes.web.core;

import javax.servlet.ServletContext;

/**
 * @ClassName: ComponentContextFactory
 * @Description: {@link ComponentContext}工厂
 * @author: zhoujian
 * @date: 2021/3/9 21:36
 * @version: 1.0
 */
public class ComponentContextFactory {

    private static ServletContext servletContext = null;

    public static void setServletContext(ServletContext servletContext){
        if (servletContext == null){
            throw new RuntimeException("ServletContext is null");
        }
        ComponentContextFactory.servletContext = servletContext;
    }

    /**
     * 获取组件上下文对象 {@link ComponentContext}
     * @author zhoujian
     * @date 22:25 2021/3/9
     * @param
     * @return org.geektimes.web.core.ComponentContext
     **/
    public static ComponentContext getComponentContext() throws RuntimeException {
        ComponentContext context = null;
        try {
            context = (ComponentContext) ComponentContextFactory.servletContext.getAttribute(ComponentContext.COMPONENT_CONTEXT_NAME);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        return context;
    }
}
