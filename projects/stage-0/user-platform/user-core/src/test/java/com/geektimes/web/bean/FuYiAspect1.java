package com.geektimes.web.bean;

import lombok.extern.slf4j.Slf4j;
import org.geektimes.web.aop.advice.AroundAdvice;
import org.geektimes.web.aop.annotation.Aspect;
import org.geektimes.web.aop.annotation.Order;

import java.lang.reflect.Method;

/**
 * @ClassName: FuYiAspect1
 * @Description: TODO
 * @author: zhoujian
 * @date: 2021/3/3 19:00
 * @version: 1.0
 */
@Slf4j
@Order(1)
@Aspect(pointcut = "@within(org.geektimes.web.core.annotation.Controller)")
public class FuYiAspect1 implements AroundAdvice {

    @Override
    public void before(Class<?> clz, Method method, Object[] args) throws Throwable {
        log.info("-----------before  FuYiAspect1-----------");
        log.info("class: {}, method: {}", clz.getName(), method.getName());
    }

    @Override
    public void afterReturning(Class<?> clz, Object returnValue, Method method, Object[] args) throws Throwable {
        log.info("-----------after  FuYiAspect1-----------");
        log.info("class: {}, method: {}", clz, method.getName());
    }

    @Override
    public void afterThrowing(Class<?> clz, Method method, Object[] args, Throwable e) {
        log.error("-----------error  FuYiAspect1-----------");
        log.error("class: {}, method: {}, exception: {}", clz, method.getName(), e.getMessage());
    }
}
