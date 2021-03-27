package org.geektimes.configuration;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.geektimes.boot.ApplicationBootstrapInitializer;
import org.geektimes.configuration.context.ConfigurationContext;
import org.geektimes.configuration.spi.UserPlatformConfigProviderResolver;
import org.geektimes.configuration.spi.source.servlet.ServletContextConfigSource;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @ClassName: ConfigurationBootstrapInitializer
 * @Description: UserPlatformConfiguration 初始化器
 * @author: zhoujian
 * @date: 2021/3/21 22:04
 * @version: 1.0
 */
public class ConfigurationBootstrapInitializer implements ApplicationBootstrapInitializer {

    public final static String CONFIG = Config.class.getName();
    public final static String CONFIG_PROVIDER_RESOLVER = ConfigProviderResolver.class.getName();
    public final static String CONFIG_BUILDER = ConfigBuilder.class.getName();

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.setAttribute(CONFIG, ConfigProvider.getConfig(servletContext.getClassLoader()));
        UserPlatformConfigProviderResolver configProviderResolver = (UserPlatformConfigProviderResolver) ConfigProviderResolver.instance();
        servletContext.setAttribute(CONFIG_PROVIDER_RESOLVER, configProviderResolver);
        ConfigBuilder configBuilder = configProviderResolver.getBuilder(servletContext.getClassLoader());
        configBuilder.withSources(new ServletContextConfigSource(servletContext));
        servletContext.setAttribute(CONFIG_BUILDER, configBuilder);
        // 注册 ConfigurationContext
        servletContext.addListener(ConfigurationContext.class);
    }

    @Override
    public int getPriority() {
        return 500;
    }
}
