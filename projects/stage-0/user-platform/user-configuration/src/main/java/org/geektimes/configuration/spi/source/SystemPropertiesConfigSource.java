package org.geektimes.configuration.spi.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName: SystemPropertiesConfigSource
 * @Description: {@link ConfigSource} SystemProperties 实现
 * @author: zhoujian
 * @date: 2021/3/14 21:01
 * @version: 1.0
 */
public class SystemPropertiesConfigSource implements ConfigSource {

    /**
     * Java 系统属性最好通过本地变量保存，使用 Map 保存，尽可能运行期不去调整
     * -Dapplication.name=user-web
     */
    private final Map<String, String> properties;

    public SystemPropertiesConfigSource() {
        Map systemProperties = System.getProperties();
        this.properties = new HashMap<>(systemProperties);
    }

    @Override
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    @Override
    public String getValue(String propertyName) {
        return properties.get(propertyName);
    }

    @Override
    public String getName() {
        return "Java System Properties";
    }
}
