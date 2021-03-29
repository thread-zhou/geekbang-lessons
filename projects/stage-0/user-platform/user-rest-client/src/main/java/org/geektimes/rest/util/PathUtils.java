package org.geektimes.rest.util;

import javax.ws.rs.Path;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * @InterfaceName: PathUtils
 * @Description: Path Utilities Class
 * @author: zhoujian
 * @date: 2021/3/27 18:42
 * @version: 1.0
 */
public interface PathUtils {

    String SLASH = "/";

    char SLASH_CHAR = SLASH.charAt(0);

    String ENCODED_SLASH = URLUtils.encode(SLASH);

    /**
     * 获取类或者方法上的 {@link Path} 参数值
     * @author zhoujian
     * @date 20:58 2021/3/29
     * @param resourceClass
     * @param handleMethod
     * @return java.lang.String
     **/
    static String resolvePath(Class<?> resourceClass, Method handleMethod) {
        String pathFromResourceClass = resolvePath(resourceClass);
        String pathFromHandleMethod = resolvePath(handleMethod);
        return pathFromResourceClass != null ? pathFromResourceClass + pathFromHandleMethod : resolvePath(handleMethod);
    }

    static String resolvePath(AnnotatedElement annotatedElement) {
        Path path = annotatedElement.getAnnotation(Path.class);
        if (path == null) {
            return null;
        }

        String value = path.value();
        if (!value.startsWith(SLASH)) {
            value = SLASH + value;
        }
        return value;
    }

    /**
     * 获取指定类指定方法签名的  {@link Path} 参数值，只取符合条件的第一条数据
     * @author zhoujian
     * @date 20:59 2021/3/29
     * @param resource
     * @param methodName
     * @return java.lang.String
     **/
    static String resolvePath(Class resource, String methodName) {
        return Stream.of(resource.getMethods())
                .filter(method -> Objects.equals(methodName, method.getName()))
                .map(PathUtils::resolvePath)
                .filter(Objects::nonNull)
                .findFirst()
                .get();
    }

    static String buildPath(String path, String... segments) {
        StringBuilder pathBuilder = new StringBuilder();

        if (path != null) {
            pathBuilder.append(path);
        }

        for (String segment : segments) {
            if (!segment.startsWith(SLASH)) {
                pathBuilder.append(SLASH);
            }
            pathBuilder.append(segment);
        }

        return pathBuilder.toString();
    }
}
