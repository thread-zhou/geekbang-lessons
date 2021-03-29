package org.geektimes.rest.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @InterfaceName: Maps
 * @Description: TODO
 * @author: zhoujian
 * @date: 2021/3/29 20:02
 * @version: 1.0
 */
public interface Maps {

    static Map of(Object... values) {
        Map map = new LinkedHashMap();
        int length = values.length;
        for (int i = 0; i < length; ) {
            map.put(values[i++], values[i++]);
        }
        return map;
    }

}
