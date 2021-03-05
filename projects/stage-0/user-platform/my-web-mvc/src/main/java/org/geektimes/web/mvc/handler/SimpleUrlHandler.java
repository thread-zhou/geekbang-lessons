package org.geektimes.web.mvc.handler;

import lombok.extern.slf4j.Slf4j;
import org.geektimes.web.FuYi;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

/**
 * @ClassName: SimpleUrlHandler
 * @Description: 普通url请求执行，主要处理静态资源
 *
 * 用于处理静态资源，当碰到资源是静态资源时就直接转发请求到Tomcat默认的servlet去
 * @author: zhoujian
 * @date: 2021/3/5 20:40
 * @version: 1.0
 */
@Slf4j
public class SimpleUrlHandler  implements Handler{

    /**
     * tomcat默认RequestDispatcher的名称
     * TODO: 其他服务器默认的RequestDispatcher.如WebLogic为FileServlet
     */
    private static final String TOMCAT_DEFAULT_SERVLET = "default";

    /**
     * 默认的RequestDispatcher,处理静态资源
     */
    private RequestDispatcher defaultServlet;

    public SimpleUrlHandler(ServletContext servletContext) {
        defaultServlet = servletContext.getNamedDispatcher(TOMCAT_DEFAULT_SERVLET);

        if (null == defaultServlet) {
            throw new RuntimeException("没有默认的Servlet");
        }

        log.info("The default servlet for serving static resource is [{}]", TOMCAT_DEFAULT_SERVLET);
    }

    @Override
    public boolean handle(RequestHandlerChain handlerChain) throws Exception {
        if (isStaticResource(handlerChain.getRequestPath())) {
            defaultServlet.forward(handlerChain.getRequest(), handlerChain.getResponse());
            return false;
        }
        return true;
    }

    /**
     * 是否为静态资源
     */
    private boolean isStaticResource(String url) {
        return url.startsWith(FuYi.getConfiguration().getAssetPath());
    }
}
