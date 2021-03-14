package org.geektimes.web.mvc.render;

import org.geektimes.web.mvc.handler.RequestHandlerChain;

/**
 * @InterfaceName: Render
 * @Description: 渲染请求结果 interface
 * @author: zhoujian
 * @date: 2021/3/5 20:24
 * @version: 1.0
 */
public interface Render {

    /**
     * 执行渲染
     */
    void render(RequestHandlerChain handlerChain) throws Exception;
}
