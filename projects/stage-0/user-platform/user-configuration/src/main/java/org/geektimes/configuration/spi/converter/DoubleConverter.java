package org.geektimes.configuration.spi.converter;

/**
 * @ClassName: StringToDoubleConverter
 * @Description: {@link Double} Converter
 * @author: zhoujian
 * @date: 2021/3/14 21:27
 * @version: 1.0
 */
public class DoubleConverter extends AbstractConverter<Double>{
    @Override
    public Double doConvert(String s) throws IllegalArgumentException, NullPointerException {
        return Double.parseDouble(s);
    }
}
