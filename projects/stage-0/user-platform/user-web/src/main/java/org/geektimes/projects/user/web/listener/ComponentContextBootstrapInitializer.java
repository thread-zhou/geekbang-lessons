package org.geektimes.projects.user.web.listener;

import org.geektimes.boot.ApplicationBootstrapInitializer;
import org.geektimes.configuration.ConfigurationBootstrapInitializer;
import org.geektimes.configuration.spi.UserPlatformConfigProviderResolver;
import org.geektimes.projects.user.spi.configuration.source.JndiConfigSource;
import org.geektimes.web.core.ComponentContext;
import org.geektimes.web.core.context.DefaultComponentContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @ClassName: ComponentContextInitializerListener
 * @Description: {@link ComponentContext} 初始化器
 *
 * 类似于 Spring#ContextLoaderListener
 *
 * @author zhoujian
 * @date: 2021/3/23 21:44
 * @version: 1.0
 */
public class ComponentContextBootstrapInitializer implements ApplicationBootstrapInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        ComponentContext componentContext = new DefaultComponentContext();
        componentContext.init(servletContext);
    }

    @Override
    public int getPriority() {
        return 777;
    }
}
