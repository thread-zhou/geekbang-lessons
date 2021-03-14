package org.geektimes.web.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这个注解的Target只有一个ElementType.FIELD，就是只能注解在属性上。
 * 意味着我们目前只实现接口注入的功能。这样可以避免构造注入造成的循环依赖问题无法解决，
 * 而且接口注入也是用的最多的方式了。如果想要实现设值方式注入大家可以自己去实现，实现原理几乎都一样。
 *
 * @author zhoujian
 * @date 22:16 2021/3/2
 **/

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
}
