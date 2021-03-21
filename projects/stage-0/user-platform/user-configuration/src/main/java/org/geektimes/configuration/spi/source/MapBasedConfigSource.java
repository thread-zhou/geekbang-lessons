package org.geektimes.configuration.spi.source;

import org.eclipse.microprofile.config.spi.ConfigSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName: MapBasedConfigSource
 * @Description: {@link org.eclipse.microprofile.config.spi.ConfigSource} 的一类实现
 *
 * 基于{@link java.util.Map}结构的实现
 *
 * @author: zhoujian
 * @date: 2021/3/20 13:21
 * @version: 1.0
 */
public abstract class MapBasedConfigSource implements ConfigSource {

    private final String name;

    private final int ordinal;

    private final Map<String, String> source;

    private final Object[] args;

    protected MapBasedConfigSource(String name, int ordinal) {
        this(name, ordinal, null);
    }

    protected MapBasedConfigSource(String name, int ordinal, Object...args){
        this.name = name;
        this.ordinal = ordinal;
        this.args = args;
        this.source = getProperties();
    }

    /**
     * 获取配置数据 Map
     *
     * @return 不可变 Map 类型的配置数据
     */
    public final Map<String, String> getProperties() {
        Map<String,String> configData = new HashMap<>();
        try {
            prepareConfig(args);
            doConfigData(configData);
        } catch (Throwable cause) {
            throw new IllegalStateException("准备配置数据发生错误",cause);
        }
        return Collections.unmodifiableMap(configData);
    }

    /**
     * 真正进行数据配置的方法
     * @author zhoujian
     * @date 22:58 2021/3/21
     * @param configData
     * @return void
     **/
    protected abstract void doConfigData(Map<String, String> configData) throws Throwable;

    /**
     * 前置准备
     * @throws Throwable
     */
    protected void prepareConfig(Object[] args) throws Throwable {

    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public final int getOrdinal() {
        return ordinal;
    }

    @Override
    public Set<String> getPropertyNames() {
        return source.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return source.get(propertyName);
    }
}
