package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.domain.User;
import org.geektimes.web.mvc.controller.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @ClassName: RestUserController
 * @Description: TODO
 * @author: zhoujian
 * @date: 2021/3/31 13:57
 * @version: 1.0
 */
@Path("/rest")
public class RestDemoController implements RestController {

    @POST
    @Path("/123/456")
    public User testPostInvocation(HttpServletRequest request, HttpServletResponse response){
        User user = new User();
        user.setName("拂衣");
        user.setEmail("hhxhxh@163.com");
        user.setId(1l);
        return user;
    }
}
