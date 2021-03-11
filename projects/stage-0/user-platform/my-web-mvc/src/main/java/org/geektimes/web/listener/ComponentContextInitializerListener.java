package org.geektimes.web.listener;

import org.geektimes.web.core.ComponentContext;
import org.geektimes.web.core.context.JndiComponentContext;
import org.geektimes.web.function.ThrowableAction;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * {@link ComponentContext} 初始化器
 *
 * 类似于 Spring#ContextLoaderListener
 *
 * @author zhoujian
 * @date 21:04 2021/3/9
 * @param 
 * @return 
 **/
public class ComponentContextInitializerListener implements ServletContextListener {

    private ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.servletContext = sce.getServletContext();
        ComponentContext componentContext = new JndiComponentContext();
        componentContext.init(servletContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ComponentContext context = (ComponentContext) this.servletContext.getAttribute(ComponentContext.COMPONENT_CONTEXT_NAME);
        if (context != null){
            ThrowableAction.execute(context::destroy);
        }
    }
}
