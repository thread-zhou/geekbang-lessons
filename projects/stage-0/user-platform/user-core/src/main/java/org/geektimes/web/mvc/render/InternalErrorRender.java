package org.geektimes.web.mvc.render;

import org.geektimes.web.mvc.handler.RequestHandlerChain;

import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName: InternalErrorRender
 * @Description: 这个Render返回StatusCode为500
 * @author: zhoujian
 * @date: 2021/3/5 20:56
 * @version: 1.0
 */
public class InternalErrorRender implements Render{
    @Override
    public void render(RequestHandlerChain handlerChain) throws Exception {
        handlerChain.getResponse().sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
