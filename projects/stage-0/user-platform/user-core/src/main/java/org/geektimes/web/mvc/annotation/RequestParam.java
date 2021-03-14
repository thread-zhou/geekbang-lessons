package org.geektimes.web.mvc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @ClassName: RequestParam
 * @Description: 请求的方法参数名
 * @author: zhoujian
 * @date: 2021/3/4 13:26
 * @version: 1.0
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {

    /**
     * 方法参数别名
     */
    String value() default "";

    /**
     * 是否必传
     */
    boolean required() default true;
}
