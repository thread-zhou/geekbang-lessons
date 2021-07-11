package org.geektimes.projects.user.web.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.geektimes.oauth2.domain.AccessToken;
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
import java.io.IOException;
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

    @GET
    @Path("/sign")
    public String sign(HttpServletRequest request, HttpServletResponse response) throws Throwable{
        return "login-form.jsp";
    }

    @GET
    @Path("/oauth")
    public String oauth(HttpServletRequest request, HttpServletResponse response){
        return "oauth-test.jsp";
    }

    @GET
    @Path("/oauth/redirect")
    public String oauthRedirect(HttpServletRequest request, HttpServletResponse response){
        return "oauth_redirect.jsp";
    }

    private final String oAuthTokenUrl = "https://gitee.com/oauth/token?grant_type=authorization_code";
    private final String clientId = "86c8fdbbf968eefac50d34fbaa9e2465adb3dbb641170e1298f291bf782e6179";
    private final String redirectUrl = "http://localhost:9090/user/oauth/redirect";
    private final String clientSecret = "10558e1272c997827a51c374274834d67078e7f8cc94562afa7ba10eba3bd9fc";

    @GET
    @Path("/oauth/token")
    public String oauthToken(HttpServletRequest request, HttpServletResponse response){
        String code = request.getParameter("code");
        System.out.println(code);
        StringBuffer buffer = new StringBuffer(oAuthTokenUrl);
        buffer.append("&code=").append(code)
                .append("&client_id=").append(clientId)
                .append("&redirect_uri=").append(redirectUrl)
                .append("&client_secret=").append(clientSecret);
        String targetUrl = buffer.toString();
        System.out.println(targetUrl);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(targetUrl);
        CloseableHttpResponse resp = null;
        try {
            resp = httpClient.execute(httpPost);
            if (resp.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(resp.getEntity(), "UTF-8");
                System.out.println(content);
                AccessToken accessToken = JSONObject.parseObject(content, AccessToken.class);
                System.out.println(accessToken);
                getUserInfo(accessToken);
                return "success.jsp";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return code;
    }

    private void getUserInfo(AccessToken accessToken) throws IOException {
        String targetUrl = "https://gitee.com/api/v5/user?access_token=" + accessToken.getAccess_token();
        System.out.println(targetUrl);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(targetUrl);
        CloseableHttpResponse resp = null;
        try {
            resp = httpClient.execute(httpGet);
            if (resp.getStatusLine().getStatusCode() == 200) {
                String content = EntityUtils.toString(resp.getEntity(), "UTF-8");
                System.out.println(content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (resp != null){
                resp.close();
            }
            if (httpClient != null){
                httpClient.close();
            }
        }
    }
}
