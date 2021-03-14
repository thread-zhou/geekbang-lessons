package org.geektimes.configuration.converter;

/**
 * @ClassName: StringToLongConverter
 * @Description: {@link Long} Converter
 * @author: zhoujian
 * @date: 2021/3/14 21:29
 * @version: 1.0
 */
public class StringToLongConverter extends AbstractConverter<Long>{
    @Override
    public Long doConvert(String s) throws IllegalArgumentException, NullPointerException {
        return Long.parseLong(s);
    }
}
