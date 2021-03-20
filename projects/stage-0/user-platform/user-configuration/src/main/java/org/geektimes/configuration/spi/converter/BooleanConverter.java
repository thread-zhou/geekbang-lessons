package org.geektimes.configuration.spi.converter;

/**
 * @ClassName: StringToBooleanConverter
 * @Description: {@link Boolean} Converter
 * @author: zhoujian
 * @date: 2021/3/14 21:29
 * @version: 1.0
 */
public class BooleanConverter extends AbstractConverter<Boolean>{
    @Override
    public Boolean doConvert(String s) throws IllegalArgumentException, NullPointerException {
        return Boolean.parseBoolean(s);
    }
}
