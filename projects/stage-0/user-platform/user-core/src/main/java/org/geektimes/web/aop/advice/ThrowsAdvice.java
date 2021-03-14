package org.geektimes.web.aop.advice;

import java.lang.reflect.Method;

/**
 * @InterfaceName: ThrowsAdvice
 * @Description: 异常通知接口
 * @author: zhoujian
 * @date: 2021/3/3 13:03
 * @version: 1.0
 */
public interface ThrowsAdvice extends Advice{

    /**
     * 异常方法
     */
    void afterThrowing(Class<?> clz, Method method, Object[] args, Throwable e);
}
