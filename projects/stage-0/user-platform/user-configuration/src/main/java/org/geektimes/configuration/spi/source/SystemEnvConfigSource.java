package org.geektimes.configuration.spi.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Map;
import java.util.Set;

/**
 * @ClassName: SystemEnvConfigSource
 * @Description: {@link ConfigSource} System.getEnv() 实现
 * @author: zhoujian
 * @date: 2021/3/15 19:28
 * @version: 1.0
 */
public class SystemEnvConfigSource implements ConfigSource {

    private final Map<String, String> envs;

    public SystemEnvConfigSource() {
      this.envs = System.getenv();
    }

    @Override
    public Set<String> getPropertyNames() {
        return this.envs.keySet();
    }

    @Override
    public String getValue(String s) {
        return this.envs.get(s);
    }

    @Override
    public String getName() {
        return "Java System Envs";
    }
}
