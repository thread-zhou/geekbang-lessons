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

    private ConcurrentMap<ClassLoader, ConfigBuilder> configBuilderRepository = new ConcurrentHashMap<>();

    private ClassLoader resolveClassLoader(ClassLoader classLoader) {
        return classLoader == null ? this.getClass().getClassLoader() : classLoader;
    }

    @Override
    public Config getConfig() {
        return getConfig(null);
    }

    @Override
    public Config getConfig(ClassLoader classLoader) {
        return configsRepository.computeIfAbsent(resolveClassLoader(classLoader), this::genericConfig);
    }

    private Config genericConfig(ClassLoader classLoader) {
        return configBuilderRepository.computeIfAbsent(classLoader, this::genericConfigBuilder).build();
    }

    private ConfigBuilder genericConfigBuilder(ClassLoader classLoader) {
        return new UserPlatformConfigBuilder(classLoader).addDefaultSources().addDiscoveredSources();
    }

    @Override
    public ConfigBuilder getBuilder() {
        return getBuilder(null);
    }

    public ConfigBuilder getBuilder(ClassLoader classLoader){
        return configBuilderRepository.computeIfAbsent(resolveClassLoader(classLoader), this::genericConfigBuilder);
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
