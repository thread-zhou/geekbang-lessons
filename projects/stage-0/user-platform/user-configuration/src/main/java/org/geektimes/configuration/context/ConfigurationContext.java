package org.geektimes.configuration.context;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.geektimes.configuration.ConfigurationBootstrapInitializer;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/**
 * @ClassName: ConfigurationContext
 * @Description: 使用 {@link ThreadLocal} 进行 {@link org.eclipse.microprofile.config.Config}
 * 与 {@link org.eclipse.microprofile.config.spi.ConfigBuilder} 的持有
 *
 * 通过实现 {@link ServletRequestListener}接口，从而达到为每一个请求复制一个线程内共享的{@link Config}
 * 与{@link ConfigBuilder}
 *
 * 这里 {@link ConfigurationContext} 和 {@link javax.servlet.ServletContext} 中持有的区别，其实并没有本质上的区别，
 * 后者需先获取到 {@link javax.servlet.ServletContext} 对象，而后通过属性获取并强制类型转换；
 * 前者其实也是通过 {@link javax.servlet.ServletContext} 进行获取，但是通过 {@link ThreadLocal} 静态化，可以在每一个
 * 请求中通过静态方法获取；
 *
 * 当然，也可以直接通过 {@link org.eclipse.microprofile.config.ConfigProvider#getConfig(ClassLoader)}、
 * {@link ConfigProvider#getConfig()} 或 {@link org.eclipse.microprofile.config.spi.ConfigProviderResolver#instance()}
 * 进行对应句柄的获取，这里的重点在于对{@link ThreadLocal}使用的学习
 *
 * @author: zhoujian
 * @date: 2021/3/24 23:09
 * @version: 1.0
 */
public class ConfigurationContext implements ServletRequestListener {

    private static final ThreadLocal<Config> CONFIG_THREAD_LOCAL = new ThreadLocal<>();

    private static final ThreadLocal<ConfigBuilder> CONFIG_BUILDER_THREAD_LOCAL = new ThreadLocal<>();

    public static Config getConfig(){
        return CONFIG_THREAD_LOCAL.get();
    }

    public static ConfigBuilder getConfigBuilder(){
        return CONFIG_BUILDER_THREAD_LOCAL.get();
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        CONFIG_THREAD_LOCAL.set((Config) servletRequestEvent.getServletContext().getAttribute(ConfigurationBootstrapInitializer.CONFIG));
        CONFIG_BUILDER_THREAD_LOCAL.set((ConfigBuilder) servletRequestEvent.getServletContext().getAttribute(ConfigurationBootstrapInitializer.CONFIG_BUILDER));
    }

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        CONFIG_THREAD_LOCAL.remove();
        CONFIG_BUILDER_THREAD_LOCAL.remove();
    }
}
