package org.geektimes.configuration.spi.converter;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * @ClassName: PrioritizedConverter
 * @Description: {@link org.eclipse.microprofile.config.spi.Converter} 排序实现
 * 装饰器模式, 支持排序
 * @author: zhoujian
 * @date: 2021/3/20 13:01
 * @version: 1.0
 */
class PrioritizedConverter<T> implements Converter<T>, Comparable<PrioritizedConverter<T>> {

    private final Converter<T> converter;

    private final int priority;

    public PrioritizedConverter(Converter<T> converter, int priority) {
        this.converter = converter;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public Converter<T> getConverter() {
        return converter;
    }

    @Override
    public T convert(String s) throws IllegalArgumentException, NullPointerException {
        return converter.convert(s);
    }

    @Override
    public int compareTo(PrioritizedConverter<T> other) {
        return Integer.compare(other.getPriority(), this.getPriority());
    }
}
