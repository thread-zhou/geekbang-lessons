package org.geektimes.web.aop;

import lombok.Getter;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @ClassName: AdviceChain
 * @Description: 通知链
 * @author: zhoujian
 * @date: 2021/3/3 18:36
 * @version: 1.0
 */
public class AdviceChain {

    /**
     * 目标类
     */
    @Getter
    private final Class<?> targetClass;
    /**
     * 目标实例
     */
    @Getter
    private final Object target;
    /**
     * 目标方法
     */
    @Getter
    private final Method method;
    /**
     * 目标方法参数
     */
    @Getter
    private final Object[] args;
    /**
     * 代理方法
     */
    private final MethodProxy methodProxy;
    /**
     * 代理通知列
     */
    private List<ProxyAdvisor> proxyList;
    /**
     * 代理通知列index
     */
    private int adviceIndex = 0;

    public AdviceChain(Class<?> targetClass, Object target, Method method, Object[] args, MethodProxy methodProxy, List<ProxyAdvisor> proxyList) {
        this.targetClass = targetClass;
        this.target = target;
        this.method = method;
        this.args = args;
        this.methodProxy = methodProxy;
        this.proxyList = proxyList;
    }

    public Object doAdviceChain() throws Throwable {
        Object result;
        while (adviceIndex < proxyList.size()
                && !proxyList.get(adviceIndex).getPointcut().matches(method)) {
            //如果当前方法不匹配切点,则略过该代理通知类
            adviceIndex++;
        }
        if (adviceIndex < proxyList.size()) {
            result = proxyList.get(adviceIndex++).doProxy(this);
        } else {
            result = methodProxy.invokeSuper(target, args);
        }
        return result;
    }
}
