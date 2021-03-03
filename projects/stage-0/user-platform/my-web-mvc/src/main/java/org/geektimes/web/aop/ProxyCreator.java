package org.geektimes.web.aop;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * @ClassName: ProxyCreator
 * @Description: 代理类创建器
 * @author: zhoujian
 * @date: 2021/3/3 13:12
 * @version: 1.0
 */
public final class ProxyCreator {

    /**
     * 创建代理类
     */
    public static Object createProxy(Class<?> targetClass, ProxyAdvisor proxyAdvisor) {
        return Enhancer.create(targetClass,
                (MethodInterceptor) (target, method, args, proxy) ->
                        proxyAdvisor.doProxy(target, targetClass, method, args, proxy));
    }
}
