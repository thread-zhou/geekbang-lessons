package org.geektimes.configuration.spi;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;
import org.geektimes.configuration.spi.converter.ConverterFactory;
import org.geektimes.configuration.spi.source.ConfigSourceFactory;

/**
 * @ClassName: UserPlatformConfigBuilder
 * @Description: {@link ConfigBuilder} 实现
 *
 * 用于构建 {@link Config}
 *
 * @author: zhoujian
 * @date: 2021/3/20 15:43
 * @version: 1.0
 */
public class UserPlatformConfigBuilder implements ConfigBuilder {

    private final ConfigSourceFactory configSourceFactory;

    private final ConverterFactory converterFactory;

    public UserPlatformConfigBuilder(ClassLoader classLoader){
        this.configSourceFactory = new ConfigSourceFactory(classLoader);
        this.converterFactory = new ConverterFactory(classLoader);
    }

    @Override
    public ConfigBuilder addDefaultSources() {
        configSourceFactory.addDefaultSources();
        return this;
    }

    @Override
    public ConfigBuilder addDiscoveredSources() {
        configSourceFactory.addDiscoveredSources();
        return this;
    }

    @Override
    public ConfigBuilder addDiscoveredConverters() {
        converterFactory.addDiscoveredConverters();
        return this;
    }

    /**
     * 替换 {@link ClassLoader}
     * @author zhoujian
     * @date 16:33 2021/3/20
     * @param classLoader
     * @return org.eclipse.microprofile.config.spi.ConfigBuilder
     **/
    @Override
    public ConfigBuilder forClassLoader(ClassLoader classLoader) {
        configSourceFactory.setClassLoader(classLoader);
        converterFactory.setClassLoader(classLoader);
        return this;
    }

    @Override
    public ConfigBuilder withSources(ConfigSource... configSources) {
        configSourceFactory.addConfigSources(configSources);
        return this;
    }

    @Override
    public ConfigBuilder withConverters(Converter<?>... converters) {
        converterFactory.addConverters(converters);
        return null;
    }

    @Override
    public <T> ConfigBuilder withConverter(Class<T> type, int priority, Converter<T> converter) {
        converterFactory.addConverter(converter, priority, type);
        return this;
    }

    @Override
    public Config build() {
        Config config = new UserPlatformConfig(configSourceFactory, converterFactory);
        return config;
    }
}
