package org.geektimes.web.mvc.render;

import org.geektimes.web.mvc.handler.RequestHandlerChain;

import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName: NotFoundRender
 * @Description: 这个Render返回StatusCode为404
 * @author: zhoujian
 * @date: 2021/3/5 20:56
 * @version: 1.0
 */
public class NotFoundRender implements Render{
    @Override
    public void render(RequestHandlerChain handlerChain) throws Exception {
        handlerChain.getResponse().sendError(HttpServletResponse.SC_NOT_FOUND);
    }
}
