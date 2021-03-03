package org.geektimes.web.aop;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodProxy;
import org.geektimes.web.aop.advice.Advice;
import org.geektimes.web.aop.advice.AfterReturningAdvice;
import org.geektimes.web.aop.advice.MethodBeforeAdvice;
import org.geektimes.web.aop.advice.ThrowsAdvice;

import java.lang.reflect.Method;

/**
 * @ClassName: ProxyAdvisor
 * @Description: 代理通知类
 *
 * 这个类就是代理类ProxyAdvisor，即到时候我们的目标类执行的时候，实际上就是执行我们这个代理类。
 * 在ProxyAdvisor中有属性Advice便是刚才编写的通知接口，然后在目标方法执行的时候，
 * 就会执行doProxy()方法，通过判定Advice接口的类型来执行在接口中实现的方法。
 *
 * 执行的顺序就是 MethodBeforeAdvice@before() -> MethodProxy@invokeSuper() -> AfterReturningAdvice@afterReturning()，
 * 如果目标方法出现异常则会执行ThrowsAdvice@afterThrowing()方法。
 * @author: zhoujian
 * @date: 2021/3/3 13:04
 * @version: 1.0
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProxyAdvisor {

    /**
     * 通知
     */
    private Advice advice;

    /**
     * 执行代理方法
     */
    public Object doProxy(Object target, Class<?> targetClass, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object result = null;

        if (advice instanceof MethodBeforeAdvice) {
            ((MethodBeforeAdvice) advice).before(targetClass, method, args);
        }
        try {
            //执行目标类的方法
            result = proxy.invokeSuper(target, args);
            if (advice instanceof AfterReturningAdvice) {
                ((AfterReturningAdvice) advice).afterReturning(targetClass, result, method, args);
            }
        } catch (Exception e) {
            if (advice instanceof ThrowsAdvice) {
                ((ThrowsAdvice) advice).afterThrowing(targetClass, method, args, e);
            } else {
                throw new Throwable(e);
            }
        }
        return result;
    }
}
