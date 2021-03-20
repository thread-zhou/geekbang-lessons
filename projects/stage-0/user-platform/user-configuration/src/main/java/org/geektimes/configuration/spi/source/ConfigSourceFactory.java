package org.geektimes.configuration.spi.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.sort;
import static java.util.ServiceLoader.load;
import static java.util.stream.Stream.of;

/**
 * @ClassName: ConfigSourceFactory
 * @Description: {@link org.eclipse.microprofile.config.spi.ConfigSource} 构建工厂
 *
 * 用于构建{@link org.eclipse.microprofile.config.spi.ConfigSource}
 *
 * 当前实现为线程不安全, 请注意使用场景
 *
 * @author: zhoujian
 * @date: 2021/3/20 15:47
 * @version: 1.0
 */
public class ConfigSourceFactory implements Iterable<ConfigSource>{

    private boolean addedDefaultConfigSources;

    private boolean addedDiscoveredConfigSources;

    private List<ConfigSource> configSources = new LinkedList<>();

    private ClassLoader classLoader;

    public ConfigSourceFactory(){
        this(Thread.currentThread().getContextClassLoader());
    }

    public ConfigSourceFactory(ClassLoader classLoader){
        this.classLoader = classLoader;
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 添加默认配置 内嵌配置
     *
     * 仅允许调用一次
     *
     * @author zhoujian
     * @date 15:53 2021/3/20
     * @param
     * @return void
     **/
    public void addDefaultSources() {
        if (addedDefaultConfigSources) {
            return;
        }
        addConfigSources(SystemPropertiesConfigSource.class,
                OperationSystemEnvironmentVariablesConfigSource.class,
                MicroprofileResourceConfigSource.class
        );
        addedDefaultConfigSources = true;
    }

    /**
     * {@link ConfigSource} 的 Discovered 模式支持
     *
     * 这里使用 SPI 技术实现, 仅允许调用一次
     *
     * @author zhoujian
     * @date 16:03 2021/3/20
     * @param
     * @return void
     **/
    public void addDiscoveredSources() {
        if (addedDiscoveredConfigSources) {
            return;
        }
        addConfigSources(load(ConfigSource.class, classLoader));
        addedDiscoveredConfigSources = true;
    }

    /**
     * 将传入的 Classes 实例化并转换为 ConfigSource[]
     * @author zhoujian
     * @date 15:58 2021/3/20
     * @param configSourceClasses
     * @return void
     **/
    public void addConfigSources(Class<? extends ConfigSource>... configSourceClasses) {
        addConfigSources(
                of(configSourceClasses)
                        .map(this::newInstance)
                        .toArray(ConfigSource[]::new)
        );
    }

    public void addConfigSources(ConfigSource... configSources) {
        addConfigSources(Arrays.asList(configSources));
    }

    /**
     * 将传入的 configSources 添加到 {@link ConfigSourceFactory#configSources}中
     *
     * 并使用 {@link ConfigSourceOrdinalComparator#INSTANCE} 对 {@link ConfigSourceFactory#configSources} 进行排序
     *
     * @author zhoujian
     * @date 15:59 2021/3/20
     * @param configSources
     * @return void
     **/
    public void addConfigSources(Iterable<ConfigSource> configSources) {
        configSources.forEach(this.configSources::add);
        sort(this.configSources, ConfigSourceOrdinalComparator.INSTANCE);
    }

    public boolean isAddedDefaultConfigSources() {
        return addedDefaultConfigSources;
    }

    public boolean isAddedDiscoveredConfigSources() {
        return addedDiscoveredConfigSources;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public Iterator<ConfigSource> iterator() {
        return configSources.iterator();
    }

    private ConfigSource newInstance(Class<? extends ConfigSource> configSourceClass) {
        ConfigSource instance = null;
        try {
            instance = configSourceClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        return instance;
    }
}
