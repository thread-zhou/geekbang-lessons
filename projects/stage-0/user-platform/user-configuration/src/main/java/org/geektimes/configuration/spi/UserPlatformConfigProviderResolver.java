package org.geektimes.configuration.spi;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @ClassName: UserPlatformConfigProviderResolver
 * @Description: {@link ConfigProviderResolver} 自定义实现
 * @author: zhoujian
 * @date: 2021/3/14 20:28
 * @version: 1.0
 */
public class UserPlatformConfigProviderResolver extends ConfigProviderResolver {

    @Override
    public Config getConfig() {
        return getConfig(null);
    }

    @Override
    public Config getConfig(ClassLoader classLoader) {
        ClassLoader targetClassLoader = classLoader;
        if (targetClassLoader == null) {
            targetClassLoader = Thread.currentThread().getContextClassLoader();
        }
        ServiceLoader<Config> configServiceLoader = ServiceLoader.load(Config.class, targetClassLoader);
        Iterator<Config> iterator = configServiceLoader.iterator();
        if (iterator.hasNext()) {
            // 获取 Config SPI 第一个实现
            return iterator.next();
        }
        throw new IllegalStateException("No Config implementation found!");
    }

    @Override
    public ConfigBuilder getBuilder() {
        return null;
    }

    @Override
    public void registerConfig(Config config, ClassLoader classLoader) {

    }

    @Override
    public void releaseConfig(Config config) {

    }
}
