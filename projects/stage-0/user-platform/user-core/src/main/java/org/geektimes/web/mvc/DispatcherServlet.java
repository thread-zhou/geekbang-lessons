package org.geektimes.web.mvc;

import lombok.extern.slf4j.Slf4j;
import org.geektimes.web.mvc.handler.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: DispatcherServlet
 * @Description: DispatcherServlet 所有http请求都由此Servlet转发
 *
 * <p>
 * 在这个类里调用了ControllerHandler和ResultRender两个类，先根据请求的方法和路径获取对应的ControllerInfo，
 * 然后再用ControllerInfo解析出对应的视图，然后就能访问到对应的页面或者返回对应的json信息了。
 *
 * 然而一直在说的所有请求都从DispatcherServlet经过好像没有体现啊，这是因为要配置web.xml才行，
 * 现在很多都在使用spring-boot的朋友可能不大清楚了，在以前使用springmvc+spring+mybatis时代的时候要写很多配置文件，
 * 其中一个就是web.xml，要在里面添加上。通过通配符*让所有请求都走的是DispatcherServlet。
 *
 *
 * <servlet>
 * 	<servlet-name>springMVC</servlet-name>
 * 	<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
 * 	<load-on-startup>1</load-on-startup>
 * 	<async-supported>true</async-supported>
 * </servlet>
 * <servlet-mapping>
 * 	<servlet-name>springMVC</servlet-name>
 * 	<url-pattern>*</url-pattern>
 * </servlet-mapping>
 *
 * 不过我们无需这样做，为了致敬spring-boot，我们会在下一节实现内嵌Tomcat，并通过启动器启动。
 * </p>
 *
 * @author: zhoujian
 * @date: 2021/3/4 18:18
 * @version: 1.0
 */
@Slf4j
public class DispatcherServlet extends HttpServlet {

    /**
     * 请求执行链
     */
    private final List<Handler> HANDLER = new ArrayList<>();

    @Override
    public void init() throws ServletException {
        HANDLER.add(new PreRequestHandler());
        HANDLER.add(new SimpleUrlHandler(getServletContext()));
        HANDLER.add(new JspHandler(getServletContext()));
        HANDLER.add(new ControllerHandler());
    }

    /**
     * 执行请求
     */
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestHandlerChain handlerChain = new RequestHandlerChain(HANDLER.iterator(), req, resp);
        handlerChain.doHandlerChain();
        handlerChain.doRender();
    }
}
