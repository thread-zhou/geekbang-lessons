package org.geektimes.projects.user.web;

import org.apache.catalina.servlets.DefaultServlet;
import org.geektimes.boot.ApplicationBootstrapInitializer;
import org.geektimes.projects.user.web.filter.CharsetEncodingFilter;
import org.geektimes.projects.user.web.listener.TestInitializerListener;
import org.geektimes.web.mvc.FrontControllerServlet;

import javax.servlet.*;
import java.util.EnumSet;

/**
 * @ClassName: ClientBootstrapInitializer
 * @Description: 客户端初始化器
 * @author: zhoujian
 * @date: 2021/3/24 19:29
 * @version: 1.0
 */
public class ClientBootstrapInitializer implements ApplicationBootstrapInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addListener(TestInitializerListener.class);

        /**
         * 注册 ServletRegistration
         **/
        ServletRegistration.Dynamic frontServlet = servletContext.addServlet("frontControllerServlet", FrontControllerServlet.class);

        /**
         * 标记容器是否在启动的时候就加载这个servlet
         * 当值为0或者大于0时，表示容器在应用启动时就加载这个servlet
         * 当是一个负数时或者没有指定时，则指示容器在该servlet被选择时才加载
         * 正数的值越小，启动该servlet的优先级越高,服务器会根据load-on-startup的大小依次对servlet进行初始化。
         * 不过即使我们将load-on-startup设置重复也不会出现异常，服务器会自己决定初始化顺序
         *
         * 配置load-on-startup后，servlet在startup后立即加载，但只是调用servlet的init()方法，用以初始化该servlet相关的资源
         * 如未配置load-on-startup，容器一般在第一次响应web请求时，会先检测该servlet是否初始化，如未初始化，则调用servlet的init()先初始化，初始化成功后，再响应请求
         **/
        frontServlet.setLoadOnStartup(0);

        // 如果有一个Servlet的url-pattern被配置为了一根正斜杠"/",这个Servlet就变成了缺省Servlet.
        // 其他Servlet都不处理的请求,由缺省Servlet来处理.
        frontServlet.addMapping("/");

        ServletRegistration.Dynamic defaultServlet = servletContext.addServlet("defaultServlet", DefaultServlet.class);
        defaultServlet.setLoadOnStartup(1);
        defaultServlet.setInitParameter("debug", "0");
        defaultServlet.setInitParameter("listings", "false");
        defaultServlet.addMapping("*.css", "*.js");

        /**
         * 注册 FilterRegistration
         **/
        FilterRegistration.Dynamic charsetEncodingFilter = servletContext.addFilter("CharsetEncodingFilter", CharsetEncodingFilter.class);
        charsetEncodingFilter.setInitParameter("encoding", "UTF-8");
        EnumSet<DispatcherType> dispatcherTypeEnumSet = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ERROR);
        charsetEncodingFilter.addMappingForUrlPatterns(dispatcherTypeEnumSet, true, "/*");
    }
}
