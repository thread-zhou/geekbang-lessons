package org.geektimes.projects.user.web.controller;

import org.apache.commons.lang.StringUtils;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.service.UserService;
import org.geektimes.web.mvc.controller.PageController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.util.Set;

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

    @Resource(name = "bean/Validator")
    private Validator validator;

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
            registerUser.setPhoneNumber("15621859465");
            Set<ConstraintViolation<User>> validations = validator.validate(registerUser);
            if (validations.size() == 0){
                if (userService.register(registerUser)){
                    // 注册成功
                    return "success.jsp";
                }
            }else {
                validations.forEach(validation -> System.out.println(validation.getMessage()));
            }
        }
        return "login-form.jsp";
    }

    @POST
    @Path("/123/456")
    public String testPostInvocation(){
        User user = new User();
        user.setEmail("zzzxxx@163.com");
        user.setName("拂衣");
        return new User().toString();
    }

    @GET
    @Path("/sign")
    public String sign(HttpServletRequest request, HttpServletResponse response) throws Throwable{
        return "login-form.jsp";
    }
}
