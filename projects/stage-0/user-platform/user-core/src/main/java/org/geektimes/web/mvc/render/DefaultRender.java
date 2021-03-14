package org.geektimes.web.mvc.render;

import org.geektimes.web.mvc.handler.RequestHandlerChain;

/**
 * @ClassName: DefaultRender
 * @Description: 默认渲染 200
 *
 * 这个是默认的Render，设置HttpServletResponse中的status为RequestHandlerChain中StatusCode。
 *
 * @author: zhoujian
 * @date: 2021/3/5 20:54
 * @version: 1.0
 */
public class DefaultRender implements Render{
    @Override
    public void render(RequestHandlerChain handlerChain) throws Exception {
        int status = handlerChain.getResponseStatus();
        // 有点流氓的感觉
        handlerChain.getResponse().setStatus(status);
    }
}
