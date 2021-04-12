package org.geektimes.boot.codec;

import io.netty.buffer.ByteBuf;

/**
 * 自定义编解码接口
 */
public interface Codec {

    /**
     * 编码动作
     * @param value
     * @return
     */
    ByteBuf encode(Object value);

    /**
     * 解码动作
     * @param value
     * @param <T>
     * @return
     */
    <T> T decode(ByteBuf value);

    /**
     * 判断是否支持
     * @param clazz
     * @return
     */
    boolean support(Class clazz);
}
