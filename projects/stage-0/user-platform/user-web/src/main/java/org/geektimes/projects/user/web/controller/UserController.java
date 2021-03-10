package org.geektimes.projects.user.web.controller;

import org.apache.commons.lang.StringUtils;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.web.mvc.controller.PageController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
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

    @Resource(name = "bean/UserService")
    private UserService userService;

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        return register(request, response);
    }

    @POST
    @Path("/register")
    public String register(HttpServletRequest request, HttpServletResponse response) throws Throwable {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (StringUtils.isNotEmpty(email) && StringUtils.isNotEmpty(password)){
            User registerUser = new User();
            registerUser.setEmail(email);
            registerUser.setName("拂衣");
            registerUser.setPassword(password);
            registerUser.setPhoneNumber("15621859466");
            if (userService.register(registerUser)){
                // 注册成功
                return "success.jsp";
            }
        }
        return "login-form.jsp";
    }

    @GET
    @Path("/sign")
    public String sign(HttpServletRequest request, HttpServletResponse response) throws Throwable{
        return "login-form.jsp";
    }
}
