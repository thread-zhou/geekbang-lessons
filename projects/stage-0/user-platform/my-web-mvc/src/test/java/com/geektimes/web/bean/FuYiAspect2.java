package com.geektimes.web.bean;

import lombok.extern.slf4j.Slf4j;
import org.geektimes.web.aop.advice.AroundAdvice;
import org.geektimes.web.aop.annotation.Aspect;
import org.geektimes.web.aop.annotation.Order;

import java.lang.reflect.Method;

/**
 * @ClassName: FuYiAspect2
 * @Description: TODO
 * @author: zhoujian
 * @date: 2021/3/3 18:58
 * @version: 1.0
 */
@Slf4j
@Order(2)
@Aspect(pointcut = "@within(org.geektimes.web.core.annotation.Controller)")
public class FuYiAspect2 implements AroundAdvice {

    @Override
    public void before(Class<?> clz, Method method, Object[] args) throws Throwable {
        log.info("-----------before  FuYiAspect2-----------");
        log.info("class: {}, method: {}", clz.getName(), method.getName());
    }

    @Override
    public void afterReturning(Class<?> clz, Object returnValue, Method method, Object[] args) throws Throwable {
        log.info("-----------after  FuYiAspect2-----------");
        log.info("class: {}, method: {}", clz, method.getName());
    }

    @Override
    public void afterThrowing(Class<?> clz, Method method, Object[] args, Throwable e) {
        log.error("-----------error  FuYiAspect2-----------");
        log.error("class: {}, method: {}, exception: {}", clz, method.getName(), e.getMessage());
    }
}
