package org.geektimes.configuration.spi.converter;

import org.eclipse.microprofile.config.spi.Converter;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

import static java.util.ServiceLoader.load;

/**
 * @ClassName: ConverterFactory
 * @Description: 用于构建 {@link Converter}
 *
 * 支持 内嵌与 SPI 拓展
 *
 * @author: zhoujian
 * @date: 2021/3/14 21:33
 * @version: 1.0
 */
public class ConverterFactory implements Iterable<Converter> {

    /**
     * 默认权重
     **/
    public static final int DEFAULT_PRIORITY = 100;

    private static List<Class<? extends Converter>> DEFAULT_CONVERTERS = new LinkedList<>();

    static {
        DEFAULT_CONVERTERS.add(BooleanConverter.class);
        DEFAULT_CONVERTERS.add(ByteConverter.class);
        DEFAULT_CONVERTERS.add(DoubleConverter.class);
        DEFAULT_CONVERTERS.add(FloatConverter.class);
        DEFAULT_CONVERTERS.add(IntegerConverter.class);
        DEFAULT_CONVERTERS.add(LongConverter.class);
        DEFAULT_CONVERTERS.add(ShortConverter.class);
        DEFAULT_CONVERTERS.add(StringConverter.class);
    }

    /**
     * {@link Converter} 缓存, 使用待转换的目标类型的 Class 作为 Key, 该类型的转换器的排序队列作为 Value
     **/
    private final Map<Class<?>, PriorityQueue<PrioritizedConverter>> typedConverters = new HashMap<>();

    private ClassLoader classLoader;

    private boolean addedDiscoveredConverters = false;

    private boolean addedDefaultConverters = false;

    public ConverterFactory() {
        this(Thread.currentThread().getContextClassLoader());
    }

    public ConverterFactory(ClassLoader classLoader) {
        this.classLoader = classLoader;
        addDefaultConverter();
    }


    public void addDefaultConverter() {
        if (addedDefaultConverters) {
            return;
        }
        addDefaultConverter(DEFAULT_CONVERTERS);
        addedDefaultConverters = true;
    }

    public void addDefaultConverter(Class<? extends Converter> ... classes){
        addConverters(Stream.of(classes).map(this::newInstance).toArray(Converter[]::new));
    }

    public void addDefaultConverter(Iterable<Class<? extends Converter>> classIterable){
        classIterable.forEach(type -> addConverter(newInstance(type)));
    }



    /**
     * {@link Converter} 的 Discovered 模式支持
     *
     * 这里使用 SPI 技术实现, 仅允许调用一次
     *
     * @author zhoujian
     * @date 16:17 2021/3/20
     * @param
     * @return void
     **/
    public void addDiscoveredConverters() {
        if (addedDiscoveredConverters) {
            return;
        }
        addConverters(load(Converter.class, classLoader));
        addedDiscoveredConverters = true;
    }

    public void addConverters(Iterable<Converter> converters) {
        converters.forEach(this::addConverter);
    }

    /**
     * 添加{@link Converter}实现, 给定默认权值: {@link ConverterFactory#DEFAULT_PRIORITY}
     * @author zhoujian
     * @date 16:23 2021/3/20
     * @param converter
     * @return void
     **/
    public void addConverter(Converter converter) {
        addConverter(converter, DEFAULT_PRIORITY);
    }

    public void addConverter(Converter converter, int priority) {
        // 计算待转换的目标类型的 Class
        Class<?> convertedType = resolveConvertedType(converter);
        addConverter(converter, priority, convertedType);
    }

    public void addConverter(Converter converter, int priority, Class<?> convertedType) {
        PriorityQueue priorityQueue = typedConverters.computeIfAbsent(convertedType, t -> new PriorityQueue<>());
        priorityQueue.offer(new PrioritizedConverter(converter, priority));
    }

    public void addConverters(Converter... converters) {
        addConverters(Arrays.asList(converters));
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * 获取 {@link Converter<T>} 的 T 类型
     * @author zhoujian
     * @date 16:20 2021/3/20
     * @param converter
     * @return java.lang.Class<?>
     **/
    protected Class<?> resolveConvertedType(Converter<?> converter) {
        assertConverter(converter);
        Class<?> convertedType = null;
        Class<?> converterClass = converter.getClass();
        while (converterClass != null) {
            convertedType = resolveConvertedType(converterClass);
            if (convertedType != null) {
                break;
            }

            Type superType = converterClass.getGenericSuperclass();
            if (superType instanceof ParameterizedType) {
                convertedType = resolveConvertedType(superType);
            }

            if (convertedType != null) {
                break;
            }
            // recursively
            converterClass = converterClass.getSuperclass();

        }

        return convertedType;
    }

    /**
     * 用于过滤 接口 与 抽象类
     * @author zhoujian
     * @date 16:20 2021/3/20
     * @param converter
     * @return void
     **/
    private void assertConverter(Converter<?> converter) {
        Class<?> converterClass = converter.getClass();
        if (converterClass.isInterface()) {
            throw new IllegalArgumentException("The implementation class of Converter must not be an interface!");
        }
        if (Modifier.isAbstract(converterClass.getModifiers())) {
            throw new IllegalArgumentException("The implementation class of Converter must not be abstract!");
        }
    }


    private Class<?> resolveConvertedType(Class<?> converterClass) {
        Class<?> convertedType = null;

        for (Type superInterface : converterClass.getGenericInterfaces()) {
            convertedType = resolveConvertedType(superInterface);
            if (convertedType != null) {
                break;
            }
        }

        return convertedType;
    }

    private Class<?> resolveConvertedType(Type type) {
        Class<?> convertedType = null;
        if (type instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            if (pType.getRawType() instanceof Class) {
                Class<?> rawType = (Class) pType.getRawType();
                if (Converter.class.isAssignableFrom(rawType)) {
                    Type[] arguments = pType.getActualTypeArguments();
                    if (arguments.length == 1 && arguments[0] instanceof Class) {
                        convertedType = (Class) arguments[0];
                    }
                }
            }
        }
        return convertedType;
    }

    public List<Converter> getConverters(Class<?> convertedType) {
        PriorityQueue<PrioritizedConverter> prioritizedConverters = typedConverters.get(convertedType);
        if (prioritizedConverters == null || prioritizedConverters.isEmpty()) {
            return Collections.emptyList();
        }
        List<Converter> converters = new LinkedList<>();
        for (PrioritizedConverter prioritizedConverter : prioritizedConverters) {
            converters.add(prioritizedConverter.getConverter());
        }
        return converters;
    }

    @Override
    public Iterator<Converter> iterator() {
        List<Converter> allConverters = new LinkedList<>();
        for (PriorityQueue<PrioritizedConverter> converters : typedConverters.values()) {
            for (PrioritizedConverter converter : converters) {
                allConverters.add(converter.getConverter());
            }
        }
        return allConverters.iterator();
    }

    public boolean isAddedDefaultConverters() {
        return addedDefaultConverters;
    }

    public boolean isAddedDiscoveredConverters() {
        return addedDiscoveredConverters;
    }

    private Converter newInstance(Class<? extends Converter> converterClass) {
        Converter instance = null;
        try {
            instance = converterClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
        return instance;
    }
}
