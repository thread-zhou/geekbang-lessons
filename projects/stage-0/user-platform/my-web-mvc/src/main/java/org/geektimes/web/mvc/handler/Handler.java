package org.geektimes.web.mvc.handler;

/**
 * @InterfaceName: Handler
 * @Description: 请求执行器 Handler
 * @author: zhoujian
 * @date: 2021/3/5 20:24
 * @version: 1.0
 */
public interface Handler {

    /**
     * 请求的执行器
     */
    boolean handle(final RequestHandlerChain handlerChain) throws Exception;
}
