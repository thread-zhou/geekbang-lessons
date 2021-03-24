package org.geektimes.configuration.context;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.eclipse.microprofile.config.spi.ConfigProviderResolver;

/**
 * @ClassName: ConfigurationContext
 * @Description: 使用 {@link ThreadLocal} 进行 {@link org.eclipse.microprofile.config.Config}
 * 与 {@link org.eclipse.microprofile.config.spi.ConfigBuilder} 的持有
 *
 * 目前也没法传入 {@link ClassLoader}
 *
 * @author: zhoujian
 * @date: 2021/3/24 23:09
 * @version: 1.0
 */
public class ConfigurationContext {

    private static final ThreadLocal<Config> configThreadLocal = new ThreadLocal<Config>(){
        @Override
        protected Config initialValue() {
            return ConfigProvider.getConfig();
        }
    };

    private static final ThreadLocal<ConfigBuilder> configBuilderThreadLocal = new ThreadLocal<ConfigBuilder>(){
        @Override
        protected ConfigBuilder initialValue() {
            return ConfigProviderResolver.instance().getBuilder();
        }
    };

    public static Config getConfig(){
        return configThreadLocal.get();
    }

    public static ConfigBuilder getConfigBuilder(){
        return configBuilderThreadLocal.get();
    }

}
