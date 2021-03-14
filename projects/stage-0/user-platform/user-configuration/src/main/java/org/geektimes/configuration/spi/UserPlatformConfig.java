package org.geektimes.configuration.spi;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigValue;
import org.eclipse.microprofile.config.spi.ConfigSource;
import org.eclipse.microprofile.config.spi.Converter;
import org.geektimes.configuration.converter.ConverterFactory;

import java.util.*;

/**
 * @ClassName: UserPlatformConfig
 * @Description: UserPlatformConfig
 * @author: zhoujian
 * @date: 2021/3/14 20:21
 * @version: 1.0
 */
public class UserPlatformConfig implements Config {

    /**
     * 内部可变的集合，不要直接暴露在外面
     */
    private List<ConfigSource> configSources = new LinkedList<>();

    private static Comparator<ConfigSource> configSourceComparator = (o1, o2) -> Integer.compare(o2.getOrdinal(), o1.getOrdinal());

    public UserPlatformConfig(){
        ServiceLoader.load(ConfigSource.class, getClass().getClassLoader()).forEach(configSources::add);
        configSources.sort(configSourceComparator);
    }

    @Override
    public <T> T getValue(String s, Class<T> aClass) {
        String propertyValue = getPropertyValue(s);
        T t = null;
        try {
            t = getConverter(aClass).get().convert(propertyValue);
        }catch (RuntimeException e){
            throw e;
        }
        return t;
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
        Set<String> propertyNames = new HashSet<>();
        configSources.forEach(configSource -> propertyNames.addAll(configSource.getPropertyNames()));
        return Collections.unmodifiableSet(propertyNames);
    }

    @Override
    public Iterable<ConfigSource> getConfigSources() {
        return Collections.unmodifiableList(configSources);
    }

    @Override
    public <T> Optional<Converter<T>> getConverter(Class<T> aClass) {
        Converter<T> convert = ConverterFactory.getConvert(aClass);
        return Optional.ofNullable(convert);
    }

    @Override
    public <T> T unwrap(Class<T> aClass) {
        return null;
    }

    protected String getPropertyValue(String propertyName){
        String propertyValue = null;
        for (ConfigSource configSource : configSources) {
            propertyValue = configSource.getValue(propertyName);
            if (propertyValue != null){
                break;
            }
        }
        return propertyValue;
    }
}
