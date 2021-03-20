package org.geektimes.configuration.spi;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @ClassName: UserPlatformConfigProviderResolver
 * @Description: {@link ConfigProviderResolver} 自定义实现
 * @author: zhoujian
 * @date: 2021/3/14 20:28
 * @version: 1.0
 */
public class UserPlatformConfigProviderResolver extends ConfigProviderResolver {

    /**
     * 缓存 {@link Config}
     **/
    private ConcurrentMap<ClassLoader, Config> configsRepository = new ConcurrentHashMap<>();

    private ClassLoader resolveClassLoader(ClassLoader classLoader) {
        return classLoader == null ? this.getClass().getClassLoader() : classLoader;
    }

    private Config loadSpi(ClassLoader classLoader){
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
    public Config getConfig() {
        return getConfig(Thread.currentThread().getContextClassLoader());
    }

    @Override
    public Config getConfig(ClassLoader classLoader) {
        return configsRepository.computeIfAbsent(classLoader, this::genericConfig);
    }

    private Config genericConfig(ClassLoader classLoader) {
        return genericConfigBuilder(resolveClassLoader(classLoader)).build();
    }

    private ConfigBuilder genericConfigBuilder(ClassLoader classLoader) {
        return new UserPlatformConfigBuilder(classLoader).addDefaultSources().addDiscoveredSources();
    }

    @Override
    public ConfigBuilder getBuilder() {
        return genericConfigBuilder(null);
    }

    @Override
    public void registerConfig(Config config, ClassLoader classLoader) {
        configsRepository.put(classLoader, config);
    }

    @Override
    public void releaseConfig(Config config) {
        List<ClassLoader> targetKeys = new LinkedList<>();
        for (Map.Entry<ClassLoader, Config> entry : configsRepository.entrySet()) {
            if (Objects.equals(config, entry.getValue())) {
                targetKeys.add(entry.getKey());
            }
        }
        targetKeys.forEach(configsRepository::remove);
    }
}
