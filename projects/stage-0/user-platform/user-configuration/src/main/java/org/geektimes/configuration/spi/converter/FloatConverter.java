package org.geektimes.configuration.spi.converter;

/**
 * @ClassName: StringToFloatConverter
 * @Description: {@link Float} Converter
 * @author: zhoujian
 * @date: 2021/3/14 21:28
 * @version: 1.0
 */
public class FloatConverter extends AbstractConverter<Float>{
    @Override
    public Float doConvert(String s) throws IllegalArgumentException, NullPointerException {
        return Float.parseFloat(s);
    }
}
