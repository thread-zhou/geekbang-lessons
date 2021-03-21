package org.geektimes.configuration.spi.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Map;

/**
 * @ClassName: SystemPropertiesConfigSource
 * @Description: {@link ConfigSource} SystemProperties 实现
 * @author: zhoujian
 * @date: 2021/3/14 21:01
 * @version: 1.0
 */
public class SystemPropertiesConfigSource extends MapBasedConfigSource{

    public SystemPropertiesConfigSource() {
        super("System Properties", 400);
    }

    /**
     * Java 系统属性最好通过本地变量保存，使用 Map 保存，尽可能运行期不去调整
     * @author zhoujian
     * @date 13:34 2021/3/20
     * @param configData
     * @return void
     **/
    @Override
    protected void doConfigData(Map configData) {
        configData.putAll(System.getProperties());
    }

}
