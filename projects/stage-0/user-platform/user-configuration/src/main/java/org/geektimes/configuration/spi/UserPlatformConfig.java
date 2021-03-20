package org.geektimes.configuration.spi;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;
import org.geektimes.configuration.spi.converter.ConverterFactory;
import org.geektimes.configuration.spi.source.ConfigSourceFactory;

import java.util.*;

import static java.util.stream.StreamSupport.stream;

/**
 * @ClassName: UserPlatformConfig
 * @Description: UserPlatformConfig
 * @author: zhoujian
 * @date: 2021/3/14 20:21
 * @version: 1.0
 */
class UserPlatformConfig implements Config {

    private final ConfigSourceFactory configSourceFactory;

    private final ConverterFactory converterFactory;

    public UserPlatformConfig(ConfigSourceFactory configSourceFactory, ConverterFactory converterFactory){
        this.configSourceFactory = configSourceFactory;
        this.converterFactory = converterFactory;
    }

    @Override
    public <T> T getValue(String propertyName, Class<T> propertyType) {
        String propertyValue = getPropertyValue(propertyName);
        Converter<T> converter = doGetConverter(propertyType);
        return converter == null ? null : converter.convert(propertyValue);
    }

    @Override
    public ConfigValue getConfigValue(String s) {
        return null;
    }

    @Override
    public <T> Optional<T> getOptionalValue(String s, Class<T> aClass) {
        return Optional.ofNullable(getValue(s, aClass));
    }

    @Override
    public Iterable<String> getPropertyNames() {
        return stream(configSourceFactory.spliterator(), false)
                .map(ConfigSource::getPropertyNames)
                .collect(LinkedHashSet::new, Set::addAll, Set::addAll);
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        return configSourceFactory;
    }

    @Override
    public <T> Optional<Converter<T>> getConverter(Class<T> forType) {
        Converter<T> converter = doGetConverter(forType);
        return converter == null ? Optional.empty() : Optional.of(converter);
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return null;
    }

    protected <T> Converter<T> doGetConverter(Class<T> forType) {
        List<Converter> converters = this.converterFactory.getConverters(forType);
        return converters.isEmpty() ? null : converters.get(0);
    }

    /**
     * 根据属性名称从众多 {@link ConfigSource} 中获取到值
     * @author zhoujian
     * @date 16:44 2021/3/20
     * @param propertyName
     * @return java.lang.String
     **/
    protected String getPropertyValue(String propertyName){
        String propertyValue = null;
        for (ConfigSource configSource : configSourceFactory) {
            propertyValue = configSource.getValue(propertyName);
            if (propertyValue != null){
                break;
            }
        }
        return propertyValue;
    }
}
