package org.geektimes.web.mvc.handler;

import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName: PreRequestHandler
 * @Description: 请求预处理
 * 用于预处理http的一些信息，比如设置http编码，处理请求url，打印一些信息等
 * @author: zhoujian
 * @date: 2021/3/5 20:38
 * @version: 1.0
 */
@Slf4j
public class PreRequestHandler implements Handler{
    @Override
    public boolean handle(RequestHandlerChain handlerChain) throws Exception {
        // 设置请求编码方式
        handlerChain.getRequest().setCharacterEncoding("UTF-8");
        String requestPath = handlerChain.getRequestPath();
        if (requestPath.length() > 1 && requestPath.endsWith("/")) {
            handlerChain.setRequestPath(requestPath.substring(0, requestPath.length() - 1));
        }
        log.info("[Doodle] {} {}", handlerChain.getRequestMethod(), handlerChain.getRequestPath());
        return true;
    }
}
