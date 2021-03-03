package org.geektimes.web.aop;

import lombok.extern.slf4j.Slf4j;
import org.geektimes.web.aop.advice.Advice;
import org.geektimes.web.aop.annotation.Aspect;
import org.geektimes.web.core.BeanContainer;

/**
 * @ClassName: Aop
 * @Description: Aop执行器
 *
 * <p>
 * AOP全称是Aspect Oriented Programming，叫做面向切面编程，和面向对象编程(OOP)一样也是一种编程思想，
 * 也是spring中一个重要的部分。
 * 其实现基于代理模式，对原来的业务进行增强。比如说原来的功能是增删改查，想要不修改源代码的情况下增强原来的功能，
 * 那么就可以对原来的业务类生成一个代理的对象，在代理对象中实现方法对原来的业务增强。
 *
 * 而代理又分静态代理和动态代理，通常我们都是用动态代理，因为静态代理都是硬编码，不适合拿来用在实现框架这种需求里。
 * 在java中通常有两种代理方式，一个是jdk自带的代理，另一个是cglib实现的代理方式，这两个代理各有特点，不大了解的话可以自行查找资料看看。
 *
 * 在spring的底层这两种代理方式都支持，在默认的情况下，如果bean实现了一个接口，spring会使用jdk代理，否则就用cglib代理。
 * 在doodle框架里用了cglib代理的方式，因为这种方式代理的类不用实现接口，实现更灵活
 *
 * 参考地址：https://zzzzbw.cn/post/11
 * </p>
 *
 * <p>
 * 虽然完成了AOP功能，但是还是有几个比较严重的缺陷的
 *
 * 对目标类的筛选不是很便捷，现在是用Aspect.target()的值，来筛选出被这个值注解的类，这样太笼统了。
 * 假如Aspect.target()=Controller.class，那么所有被Controller注解的controller里的左右方法都要被代理。
 * 我们希望能够像spring那样如execution(* com.zbw.*.service..*Impl.*(..)),用一些表达式来筛选目标类。
 * 一个目标类只能被一个切面作用。目前来说比如有DoodleAspect1和DoodleAspect2两个切面，都作用于DoodleController上，
 * 只有一个切面能生效，这也不合理。
 * </p>
 * @author: zhoujian
 * @date: 2021/3/3 13:09
 * @version: 1.0
 */
@Slf4j
public class Aop {

    /**
     * Bean容器
     */
    private BeanContainer beanContainer;

    public Aop() {
        beanContainer = BeanContainer.getInstance();
    }

    public void doAop() {
        beanContainer.getClassesBySuper(Advice.class)
                .stream()
                .filter(clz -> clz.isAnnotationPresent(Aspect.class))
                .forEach(clz -> {
                    final Advice advice = (Advice) beanContainer.getBean(clz);
                    Aspect aspect = clz.getAnnotation(Aspect.class);
                    beanContainer.getClassesByAnnotation(aspect.target())
                            .stream()
                            .filter(target -> !Advice.class.isAssignableFrom(target))
                            .filter(target -> !target.isAnnotationPresent(Aspect.class))
                            .forEach(target -> {
                                ProxyAdvisor advisor = new ProxyAdvisor(advice);
                                Object proxyBean = ProxyCreator.createProxy(target, advisor);
                                beanContainer.addBean(target, proxyBean);
                            });
                });
    }
}
