package org.geektimes.web.aop.advice;

import java.lang.reflect.Method;

/**
 * @InterfaceName: MethodBeforeAdvice
 * @Description: 前置通知接口
 * @author: zhoujian
 * @date: 2021/3/3 13:01
 * @version: 1.0
 */
public interface MethodBeforeAdvice extends Advice{

    /**
     * 前置方法
     */
    void before(Class<?> clz, Method method, Object[] args) throws Throwable;
}
