package org.geektimes.configuration.converter;

import org.eclipse.microprofile.config.spi.Converter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @ClassName: ConverterFactory
 * @Description: ConverterFactory
 * @author: zhoujian
 * @date: 2021/3/14 21:33
 * @version: 1.0
 */
public final class ConverterFactory {

    private static Map<Class, Converter> converterMap = new LinkedHashMap<>();

    static {
        converterMap.put(Byte.class, new StringToByteConverter());
        converterMap.put(Boolean.class, new StringToBooleanConverter());
        converterMap.put(Double.class, new StringToDoubleConverter());
        converterMap.put(Float.class, new StringToFloatConverter());
        converterMap.put(Integer.class, new StringToIntegerConverter());
        converterMap.put(Long.class, new StringToLongConverter());
        converterMap.put(Short.class, new StringToShortConverter());
        converterMap.put(String.class, new StringToStringConverter());
    }

    public static Converter getConvert(Class clc){
        return converterMap.get(clc);
    }
}
