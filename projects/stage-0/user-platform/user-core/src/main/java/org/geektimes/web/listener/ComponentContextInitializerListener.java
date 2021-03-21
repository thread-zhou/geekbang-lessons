package org.geektimes.web.listener;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.geektimes.web.FuYi;
import org.geektimes.web.core.ComponentContext;
import org.geektimes.web.core.context.DefaultComponentContext;
import org.geektimes.web.function.ThrowableAction;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Iterator;
import java.util.ServiceLoader;

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
    public final static String CONFIG = Config.class.getName();
    public final static String CONFIG_PROVIDER_RESOLVER = ConfigProviderResolver.class.getName();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.servletContext = sce.getServletContext();
        ComponentContext componentContext = new DefaultComponentContext();
        componentContext.init(servletContext);
        servletContext.setAttribute(CONFIG, ConfigProvider.getConfig(sce.getServletContext().getClassLoader()));
        servletContext.setAttribute(CONFIG_PROVIDER_RESOLVER, ConfigProviderResolver.instance());
    }

    private ConfigProviderResolver loadSpi(){
        ServiceLoader<ConfigProviderResolver> configProviderResolverServiceLoader = ServiceLoader.load(ConfigProviderResolver.class);
        Iterator<ConfigProviderResolver> configProviderResolverIterator = configProviderResolverServiceLoader.iterator();
        if (configProviderResolverIterator.hasNext()){
            return configProviderResolverIterator.next();
        }
        throw new RuntimeException("Not Fount Any ConfigProviderResolver");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ComponentContext context = (ComponentContext) this.servletContext.getAttribute(ComponentContext.COMPONENT_CONTEXT_NAME);
        if (context != null){
            ThrowableAction.execute(context::destroy);
        }
    }
}
