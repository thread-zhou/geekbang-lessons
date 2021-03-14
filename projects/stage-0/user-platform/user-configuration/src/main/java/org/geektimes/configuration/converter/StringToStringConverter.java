package org.geektimes.configuration.converter;

/**
 * @ClassName: StringToStringConverter
 * @Description: {@link String} Converter
 * @author: zhoujian
 * @date: 2021/3/14 21:26
 * @version: 1.0
 */
public class StringToStringConverter extends AbstractConverter<String>{
    @Override
    public String doConvert(String s) throws IllegalArgumentException, NullPointerException {
        return s;
    }
}
