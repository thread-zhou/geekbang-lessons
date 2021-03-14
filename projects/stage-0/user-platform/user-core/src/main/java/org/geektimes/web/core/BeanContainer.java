package org.geektimes.web.core;

import lombok.extern.slf4j.Slf4j;
import org.geektimes.web.aop.annotation.Aspect;
import org.geektimes.web.core.annotation.Component;
import org.geektimes.web.core.annotation.Controller;
import org.geektimes.web.core.annotation.Repository;
import org.geektimes.web.core.annotation.Service;
import org.geektimes.web.util.ClassUtil;

import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @ClassName: BeanContainer
 * @Description: Bean容器
 *
 * 实际上就是存放所有Bean的地方，即Class以及相关信息对应其实体的容器，
 * 为什么称之为'Bean'呢，因为在spring中，定义Class信息和实例的东西叫BeanDefinition。
 * 这是一个接口，他有一个模板类AbstractBeanDefinition，
 * 这里面就有一个beanClass变量存放Class类和propertyValues变量存放类属性，
 * 以及很多类相关参数和初始化之类的参数。大家可以去spring中看看，spring的所有都是依赖于这个Bean生成的，
 * 可以说这是spring的基石
 *
 * 我们不需要像spring那样存放很多的信息，所以用一个Map来存储Bean的信息就好了。Map的Key为Class类，Value为这个Class的实例Object
 *
 * @author: zhoujian
 * @date: 2021/3/2 21:50
 * @version: 1.0
 */
@Slf4j
public class BeanContainer {

    /**
     * 是否加载Bean
     */
    private boolean isLoadBean = false;

    /**
     * 加载bean的注解列表
     */
    private static final List<Class<? extends Annotation>> BEAN_ANNOTATION
            = Arrays.asList(Component.class, Controller.class, Service.class, Repository.class, Aspect.class);

    /**
     * 存放所有Bean的Map
     */
    private final Map<Class<?>, Object> beanMap = new ConcurrentHashMap<>();

    /**
     * 是否加载Bean
     */
    public boolean isLoadBean() {
        return isLoadBean;
    }

    /**
     * 获取Bean实例
     */
    public Object getBean(Class<?> clz) {
        if (null == clz) {
            return null;
        }
        return beanMap.get(clz);
    }

    /**
     * 获取所有Bean集合
     *
     * HashSet 基于 HashMap 来实现的，是一个不允许有重复元素的集合。
     *
     * HashSet 允许有 null 值。
     *
     * HashSet 是无序的，即不会记录插入的顺序。
     *
     * HashSet 不是线程安全的， 如果多个线程尝试同时修改 HashSet，则最终结果是不确定的。 您必须在多线程访问时显式同步对 HashSet 的并发访问。
     *
     * HashSet 实现了 Set 接口。
     */
    public Set<Object> getBeans() {
        return new HashSet<>(beanMap.values());
    }

    /**
     * 添加一个Bean实例
     */
    public Object addBean(Class<?> clz, Object bean) {
        return beanMap.put(clz, bean);
    }

    /**
     * 移除一个Bean实例
     */
    public void removeBean(Class<?> clz) {
        beanMap.remove(clz);
    }

    /**
     * Bean实例数量
     */
    public int size() {
        return beanMap.size();
    }

    /**
     * 所有Bean的Class集合
     */
    public Set<Class<?>> getClasses() {
        return beanMap.keySet();
    }

    /**
     * 通过注解获取Bean的Class集合
     */
    public Set<Class<?>> getClassesByAnnotation(Class<? extends Annotation> annotation) {
        return beanMap.keySet()
                .stream()
                .filter(clz -> clz.isAnnotationPresent(annotation))
                .collect(Collectors.toSet());
    }

    /**
     * 通过实现类或者父类获取Bean的Class集合（获取给定接口或超类的实现或子类）
     *
     * isAssignableFrom()方法是从类继承的角度去判断，instanceof()方法是从实例继承的角度去判断
     *
     * isAssignableFrom()方法是判断是否为某个类的超类或接口，instanceof()方法是判断是否某个类的子类
     */
    public Set<Class<?>> getClassesBySuper(Class<?> superClass) {
        return beanMap.keySet()
                .stream()
                .filter(superClass::isAssignableFrom)
                .filter(clz -> !clz.equals(superClass))
                .collect(Collectors.toSet());
    }


    /**
     * 扫描加载所有Bean
     */
    public void loadBeans(String basePackage) {
        if (isLoadBean()) {
            log.warn("bean已经加载");
            return;
        }

        Set<Class<?>> classSet = ClassUtil.getPackageClass(basePackage);
        classSet.stream()
                .filter(clz -> {
                    for (Class<? extends Annotation> annotation : BEAN_ANNOTATION) {
                        /**
                         * 如果指定类型的注释存在于此元素上，返回true，否则返回false
                         **/
                        if (clz.isAnnotationPresent(annotation)) {
                            return true;
                        }
                    }
                    return false;
                })
                .forEach(clz -> beanMap.put(clz, ClassUtil.newInstance(clz)));
        isLoadBean = true;
    }

    /**
     * 获取 BeanContainer 实例
     **/
    public static BeanContainer getInstance() {
        return ContainerHolder.HOLDER.instance;
    }

    private enum ContainerHolder {
        HOLDER;
        private BeanContainer instance;

        ContainerHolder() {
            instance = new BeanContainer();
        }
    }
}
