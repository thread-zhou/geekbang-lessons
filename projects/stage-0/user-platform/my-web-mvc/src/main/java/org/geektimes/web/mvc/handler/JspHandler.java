package org.geektimes.web.mvc.handler;

import lombok.extern.slf4j.Slf4j;
import org.geektimes.web.FuYi;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;

/**
 * @ClassName: JspHandler
 * @Description: 当碰到资源是jsp页面时就直接转发请求到Tomcat的jsp的servlet去
 * @author: zhoujian
 * @date: 2021/3/5 20:45
 * @version: 1.0
 */
@Slf4j
public class JspHandler implements Handler{

    /**
     * jsp请求的RequestDispatcher的名称
     */
    private static final String JSP_SERVLET = "jsp";

    /**
     * jsp的RequestDispatcher,处理jsp资源
     */
    private RequestDispatcher jspServlet;

    public JspHandler(ServletContext servletContext) {
        jspServlet = servletContext.getNamedDispatcher(JSP_SERVLET);
        if (null == jspServlet) {
            throw new RuntimeException("没有jsp Servlet");
        }
        log.info("The default servlet for serving jsp is [{}]", JSP_SERVLET);
    }

    @Override
    public boolean handle(final RequestHandlerChain handlerChain) throws Exception {
        if (isPageView(handlerChain.getRequestPath())) {
            jspServlet.forward(handlerChain.getRequest(), handlerChain.getResponse());
            return false;
        }
        return true;
    }

    /**
     * 是否为jsp资源
     */
    private boolean isPageView(String url) {
        return url.startsWith(FuYi.getConfiguration().getViewPath());
    }
}
