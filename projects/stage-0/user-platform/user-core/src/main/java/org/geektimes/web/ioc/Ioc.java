package org.geektimes.web.ioc;

import lombok.extern.slf4j.Slf4j;
import org.geektimes.web.core.BeanContainer;
import org.geektimes.web.core.annotation.Autowired;
import org.geektimes.web.util.ClassUtil;

import java.lang.reflect.Field;
import java.util.Optional;

/**
 * @ClassName: Ioc
 * @Description:
 * IoC全称是Inversion of Control，就是控制反转，他其实不是spring独有的特性或者说也不是java的特性，他是一种设计思想。
 * 而DI(Dependency Injection)，即依赖注入就是Ioc的一种实现方式。关于Ioc和DI的具体定义和优缺点等大家可以自行查找资料了解一下，
 * 这里就不详细赘述，总之spring的IoC功能很大程度上便捷了我们的开发工作。
 *
 * <p>
 * 在实现我们的Ioc之前，我们先了解一下spring的依赖注入，在spring中依赖注入有三种方式，分别是：
 *
 * 接口注入(Interface Injection)
 * 设值方法注入(Setter Injection)
 * 构造注入(Constructor Injection)
 * </p>
 *
 * <p>
 * 循环依赖注入：
 *
 * 如果只是实现依赖注入的话实际上很简单，只要利用java的反射原理将对应的属性‘注入’进去就可以了。
 * 但是必须要注意一个问题，那就是循环依赖问题。循环依赖就是类之间相互依赖形成了一个循环，比如A依赖于B，同时B又依赖于A，这就形成了相互循环
 *
 * 那么在spring中又是如何解决循环依赖问题的呢，我们大致说一下原理。
 *
 * 如果要创建一个类，先把这个类放进'正在创建池'中，通过反射等创建实例，创建成功的话就把这个实例放入创建池中，并移除'正在创建池'中的这个类。
 * 每当实例中有依赖需要注入的话，就从创建池中找对应的实例注入进去，如果没有找到实例，则先创建这个依赖。
 *
 * 利用了这个正在创建的中间状态缓存，让Bean的创建的时候即使有依赖还没有实例化，可以先把Bean放进这个中间状态，然后跑去创建那个依赖，
 * 假如那个依赖的类又依赖与这个Bean，那么只要在'正在创建池'中再把这个Bean拿出来，注入到这个依赖中，就可以保证Bean的依赖能够实例化完成。
 * 再回头来把这个依赖注入到Bean中，那么这个Bean也实例化完成了，就把这个Bean从'正在创建池'移到'创建完成池'中，就解决了循环依赖问题。
 *
 * 虽然spring巧妙的避免了循环依赖问题，但是事实上构造注入是无法避免循环依赖问题的。因为在实例化ComponentA的构造函数的时候必须得到ComponentB的实例，
 * 但是实例化ComponentB的构造函数的时候又必须有ComponentA的实例。这两个Bean都不能通过反射实例化然后放到'正在创建池'，所以无法解决循环依赖问题，
 * 这时候spring就会主动抛出BeanCurrentlyInCreationException异常避免死循环。
 *
 * 注意，前面讲的这些都是基于spring的单例模式下的，如果是多例模式会有所不同，大家有兴趣可以自行了解。
 * </p>
 *
 * @author: zhoujian
 * @date: 2021/3/2 22:17
 * @version: 1.0
 */
@Slf4j
public class Ioc {

    /**
     * Bean 容器
     **/
    private BeanContainer beanContainer;

    public Ioc() {
        beanContainer = BeanContainer.getInstance();
    }

    /**
     * 执行Ioc
     *
     * 然后在doIoc()方法中就是正式实现IOC功能的了。
     *
     * 1、遍历在BeanContainer容器的所有Bean
     * 2、对每个Bean的Field属性进行遍历
     * 3、如果某个Field属性被Autowired注解，则调用getClassInstance()方法对其进行注入
     * 4、getClassInstance()会根据Field的Class尝试从Bean容器中获取对应的实例，
     * 如果获取到则返回该实例，如果获取不到，则我们认定该Field为一个接口，
     * 我们就调用getImplementClass()方法来获取这个接口的实现类Class，
     * 然后再根据这个实现类Class在Bean容器中获取对应的实现类实例。
     */
    public void doIoc() {
        //遍历Bean容器中所有的Bean
        for (Class<?> clz : beanContainer.getClasses()) {
            final Object targetBean = beanContainer.getBean(clz);
            Field[] fields = clz.getDeclaredFields();

            //遍历Bean中的所有属性
            for (Field field : fields) {
                // 如果该属性被Autowired注解，则对其注入
                if (field.isAnnotationPresent(Autowired.class)) {
                    final Class<?> fieldClass = field.getType();
                    Object fieldValue = getClassInstance(fieldClass);
                    if (null != fieldValue) {
                        ClassUtil.setField(field, targetBean, fieldValue);
                    } else {
                        throw new RuntimeException("无法注入对应的类，目标类型:" + fieldClass.getName());
                    }
                }
            }
        }
    }

    /**
     * 根据Class获取其实例或者实现类
     *
     * Optional 类是一个可以为null的容器对象。如果值存在则isPresent()方法会返回true，调用get()方法会返回该对象。
     *
     * Optional 是个容器：它可以保存类型T的值，或者仅仅保存null。Optional提供很多有用的方法，这样我们就不用显式进行空值检测。
     *
     * Optional 类的引入很好的解决空指针异常。
     */
    private Object getClassInstance(final Class<?> clz) {
        return Optional
                // 如果为非空，返回 Optional 描述的指定值，否则返回空的 Optional。
                .ofNullable(beanContainer.getBean(clz))
                // 如果存在该值，返回值， 否则触发 other，并返回 other 调用的结果。
                .orElseGet(() -> {
                    // 表示不存在该值，当下为 other调用
                    Class<?> implementClass = getImplementClass(clz);
                    if (null != implementClass) {
                        return beanContainer.getBean(implementClass);
                    }
                    return null;
                });
    }

    /**
     * 获取接口的实现类
     */
    private Class<?> getImplementClass(final Class<?> interfaceClass) {
        return beanContainer.getClassesBySuper(interfaceClass)
                .stream()
                .findFirst()
                // 如果存在该值，返回值， 否则返回 other。 此处other 为 null
                .orElse(null);
    }
}
