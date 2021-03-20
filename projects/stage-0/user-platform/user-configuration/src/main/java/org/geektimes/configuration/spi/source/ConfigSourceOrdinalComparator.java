package org.geektimes.configuration.spi.source;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.util.Comparator;

/**
 * @ClassName: ConfigSourceOrdinalComparator
 * @Description: {@link ConfigSource} 优先级比较器
 * @author: zhoujian
 * @date: 2021/3/20 13:48
 * @version: 1.0
 */
public class ConfigSourceOrdinalComparator implements Comparator<ConfigSource>{

    /**
     * Singleton instance {@link ConfigSourceOrdinalComparator}
     */
    public static final Comparator<ConfigSource> INSTANCE = new ConfigSourceOrdinalComparator();


    private ConfigSourceOrdinalComparator() {
    }

    /**
     * 排序, 权重按照整数倒序排列
     * @author zhoujian
     * @date 13:50 2021/3/20
     * @param o1
     * @param o2
     * @return int
     **/
    @Override
    public int compare(ConfigSource o1, ConfigSource o2) {
        return Integer.compare(o2.getOrdinal(), o1.getOrdinal());
    }
}
