package org.geektimes.web.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geektimes.web.core.BeanContainer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.stream.Collectors;

/**
 * @ClassName: ClassUtil
 * @Description: 主要是Class的一些相关操作
 * 这其中除了一些类常用的实例反射等操作，还有一个重要方法就是
 * getPackageClass()，这个方法会递归遍历传入的包名下的所有类文件，
 * 并返回一个Set<Class<?>>。等一下在实现Bean容器的时候就会使用这
 * 个方法来扫描获取对应包下的所有类文件
 *
 * 最后，为了能够保证整个项目全局Bean的唯一性，我们要保证这个BeanContainer是唯一的，将该类单例化，
 * 通过lombok的注解@NoArgsConstructor(access = AccessLevel.PRIVATE)生成私有构造函数，
 * 再用内部枚举生成唯一的BeanContainer实例
 *
 * 参考地址：https://zzzzbw.cn/post/9
 * @author: zhoujian
 * @date: 2021/3/2 21:34
 * @version: 1.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ClassUtil {

    /**
     * file形式url协议
     */
    public static final String FILE_PROTOCOL = "file";

    /**
     * jar形式url协议
     */
    public static final String JAR_PROTOCOL = "jar";

    /**
     * 获取classLoader
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 获取Class
     */
    public static Class<?> loadClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("load class error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 实例化class
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className) {
        try {
            Class<?> clazz = loadClass(className);
            return (T) clazz.newInstance();
        } catch (Exception e) {
            log.error("newInstance error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 实例化class
     */
    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<?> clazz) {
        try {
            return (T) clazz.newInstance();
        } catch (Exception e) {
            log.error("newInstance error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 设置类的属性值
     */
    public static void setField(Field field, Object target, Object value) {
        setField(field, target, value, true);
    }

    /**
     * 设置类的属性值
     */
    public static void setField(Field field, Object target, Object value, boolean accessible) {
        field.setAccessible(accessible);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            log.error("setField error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取包下类集合
     */
    public static Set<Class<?>> getPackageClass(String basePackage) {
        URL url = getClassLoader()
                .getResource(basePackage.replace(".", "/"));
        if (null == url) {
            throw new RuntimeException("无法获取项目路径文件");
        }
        try {
            if (url.getProtocol().equalsIgnoreCase(FILE_PROTOCOL)) {
                // 若为普通文件夹，则遍历
                File file = new File(url.getFile());
                Path basePath = file.toPath();
                return Files.walk(basePath)
                        .filter(path -> path.toFile().getName().endsWith(".class"))
                        .map(path -> getClassByPath(path, basePath, basePackage))
                        .collect(Collectors.toSet());
            } else if (url.getProtocol().equalsIgnoreCase(JAR_PROTOCOL)) {
                // 若在 jar 包中，则解析 jar 包中的 entry
                JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                return jarURLConnection.getJarFile()
                        .stream()
                        .filter(jarEntry -> jarEntry.getName().endsWith(".class"))
                        .filter(jarEntry -> getJarEntryFileClassPath(jarEntry).startsWith(basePackage))
                        .map(ClassUtil::getClassByJar)
                        .collect(Collectors.toSet());
            }
            return Collections.emptySet();
        } catch (IOException e) {
            log.error("load package error", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 从Path获取Class
     */
    private static Class<?> getClassByPath(Path classPath, Path basePath, String basePackage) {
        String packageName = classPath.toString().replace(basePath.toString(), "");
        String className = (basePackage + packageName)
                .replace("/", ".")
                .replace("\\", ".")
                .replace(".class", "");
        return loadClass(className);
    }

    /**
     * 从jar包获取Class
     */
    private static Class<?> getClassByJar(JarEntry jarEntry) {
        String jarEntryClassPath = getJarEntryFileClassPath(jarEntry);
        // 获取类名
        String className = jarEntryClassPath.substring(0, jarEntryClassPath.lastIndexOf("."));
        return loadClass(className);
    }

    /**
     * org/geektimes/App.class -> org.geektimes.App.class
     **/
    private static String getJarEntryFileClassPath(JarEntry jarEntry){
        return jarEntry.getName().replaceAll("/", ".");
    }

}
