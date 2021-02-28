package org.geektimes.projects.user.web.controller;

import org.geektimes.web.mvc.controller.PageController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @ClassName: UserController
 * @Description: 用户 Controller, 当下仅提供注册方法实现
 * @author: zhoujian
 * @date: 2021/2/28 22:13
 * @version: 1.0
 */
@Path("/user")
public class UserController implements PageController {

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        return null;
    }

    @POST
    @Path("/register")
    public String register(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        return "index.jsp";
    }
}
