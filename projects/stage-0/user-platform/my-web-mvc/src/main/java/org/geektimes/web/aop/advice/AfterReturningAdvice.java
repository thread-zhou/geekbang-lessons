package org.geektimes.web.aop.advice;

import java.lang.reflect.Method;

/**
 * @InterfaceName: AfterReturningAdvice
 * @Description: 返回通知接口
 * @author: zhoujian
 * @date: 2021/3/3 13:02
 * @version: 1.0
 */
public interface AfterReturningAdvice extends Advice{

    /**
     * 返回后方法
     */
    void afterReturning(Class<?> clz, Object returnValue, Method method, Object[] args) throws Throwable;
}
