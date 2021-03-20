package org.geektimes.configuration.spi.converter;

/**
 * @ClassName: StringToShortConverter
 * @Description: {@link Short} Converter
 * @author: zhoujian
 * @date: 2021/3/14 21:31
 * @version: 1.0
 */
public class ShortConverter extends AbstractConverter<Short>{
    @Override
    public Short doConvert(String s) throws IllegalArgumentException, NullPointerException {
        return Short.parseShort(s);
    }
}
