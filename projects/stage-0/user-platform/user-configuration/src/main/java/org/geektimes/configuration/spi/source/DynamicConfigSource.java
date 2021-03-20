package org.geektimes.configuration.spi.source;

import java.util.Collections;
import java.util.Map;

/**
 * @ClassName: DynamicConfigSource
 * @Description: {@link org.eclipse.microprofile.config.spi.ConfigSource} 动态配置源
 * @author: zhoujian
 * @date: 2021/3/20 13:46
 * @version: 1.0
 */
public class DynamicConfigSource extends MapBasedConfigSource{

    private Map configData;

    public DynamicConfigSource() {
        super("Dynamic ConfigSource", 500);
    }

    @Override
    protected void prepareConfigData(Map configData) throws Throwable {
        configData = Collections.EMPTY_MAP;
        // TODO: 2021/3/20 完成动态配置源的初始化
    }

    public void onUpdate(String data) {
        // 更新（异步）
    }
}
