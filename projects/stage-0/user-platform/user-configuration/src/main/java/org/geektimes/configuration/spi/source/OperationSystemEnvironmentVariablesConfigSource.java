package org.geektimes.configuration.spi.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Map;

/**
 * @ClassName: OperationSystemEnvironmentVariablesConfigSource
 * @Description: {@link ConfigSource} System.getEnv() 实现
 *
 * 存储操作系统环境变量
 *
 * @author: zhoujian
 * @date: 2021/3/15 19:28
 * @version: 1.0
 */
public class OperationSystemEnvironmentVariablesConfigSource extends MapBasedConfigSource {

    public OperationSystemEnvironmentVariablesConfigSource() {
      super("Operation System Environment Variables", 300);
    }

    @Override
    protected void doConfigData(Map configData) {
        configData.putAll(System.getenv());
    }
}
