package org.geektimes.configuration.spi.converter;

/**
 * @ClassName: StringToIntegerConverter
 * @Description: {@link Integer} Converter
 * @author: zhoujian
 * @date: 2021/3/14 21:14
 * @version: 1.0
 */
public class IntegerConverter extends AbstractConverter<Integer>{
    @Override
    public Integer doConvert(String s) throws IllegalArgumentException, NullPointerException {
        return Integer.parseInt(s);
    }
}
