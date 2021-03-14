package org.geektimes.configuration.converter;

/**
 * @ClassName: StringToByteConverter
 * @Description: {@link Byte} Converter
 * @author: zhoujian
 * @date: 2021/3/14 21:31
 * @version: 1.0
 */
public class StringToByteConverter extends AbstractConverter<Byte>{
    @Override
    public Byte doConvert(String s) throws IllegalArgumentException, NullPointerException {
        return Byte.parseByte(s);
    }
}
