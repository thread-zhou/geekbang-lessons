package com.geektimes.web.bean;

import lombok.extern.slf4j.Slf4j;
import org.geektimes.web.aop.advice.AroundAdvice;
import org.geektimes.web.aop.annotation.Aspect;
import org.geektimes.web.core.annotation.Controller;

import java.lang.reflect.Method;

/**
 * @ClassName: FuYiAspect
 * @Description: TODO
 * @author: zhoujian
 * @date: 2021/3/3 13:24
 * @version: 1.0
 */
@Slf4j
@Aspect(target = Controller.class)
public class FuYiAspect implements AroundAdvice {

    @Override
    public void before(Class<?> clz, Method method, Object[] args) throws Throwable {
        log.info("Before  FuYiAspect ----> class: {}, method: {}", clz.getName(), method.getName());
    }

    @Override
    public void afterReturning(Class<?> clz, Object returnValue, Method method, Object[] args) throws Throwable {
        log.info("After  FuYiAspect ----> class: {}, method: {}", clz, method.getName());
    }

    @Override
    public void afterThrowing(Class<?> clz, Method method, Object[] args, Throwable e) {
        log.error("Error  FuYiAspect ----> class: {}, method: {}, exception: {}", clz, method.getName(), e.getMessage());
    }
}
