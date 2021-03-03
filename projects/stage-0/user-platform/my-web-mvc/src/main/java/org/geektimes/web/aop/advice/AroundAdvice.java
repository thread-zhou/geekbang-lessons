package org.geektimes.web.aop.advice;

/**
 * @InterfaceName: AroundAdvice
 * @Description: 环绕通知接口
 * @author: zhoujian
 * @date: 2021/3/3 13:04
 * @version: 1.0
 */
public interface AroundAdvice extends MethodBeforeAdvice, AfterReturningAdvice, ThrowsAdvice{
}
