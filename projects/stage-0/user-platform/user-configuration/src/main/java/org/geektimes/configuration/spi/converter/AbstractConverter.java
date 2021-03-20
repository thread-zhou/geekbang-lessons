package org.geektimes.configuration.spi.converter;

import org.eclipse.microprofile.config.spi.Converter;

/**
 * @ClassName: AbstractConverter
 * @Description: {@link Converter} 公共实现
 * @author: zhoujian
 * @date: 2021/3/14 21:15
 * @version: 1.0
 */
public abstract class AbstractConverter<T> implements Converter<T> {

    @Override
    public T convert(String s) throws IllegalArgumentException, NullPointerException {
        if (s == null) {
            throw new NullPointerException("The value must not be null!");
        }
        return doConvert(s);
//        Class<T> tClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//        if (CastUtil.isPrimitive(tClass)){
//            return doConvert(s);
//        }
//        throw new IllegalArgumentException("暂不支持非原生类型: " + tClass.getName());
    }

    public abstract T doConvert(String s) throws IllegalArgumentException, NullPointerException;

}
