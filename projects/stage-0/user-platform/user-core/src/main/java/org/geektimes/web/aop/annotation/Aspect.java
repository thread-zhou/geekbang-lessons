package org.geektimes.web.aop.annotation;

import java.lang.annotation.*;

/**
 * 这个注解是用于标记在'切面'中，即实现代理功能的类上面
 * @author zhoujian
 * @date 13:00 2021/3/3
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {

    /**
     * 目标代理类的范围
     * @author zhoujian
     * @date 12:59 2021/3/3
     * @param
     * @return java.lang.Class<? extends Annotation>
     **/
    String pointcut() default "";
}
