package org.geektimes.projects.user.web.controller;

import org.geektimes.web.core.annotation.Controller;
import org.geektimes.web.mvc.annotation.RequestMapping;
import org.geektimes.web.mvc.annotation.ResponseBody;

/**
 * @ClassName: FuYiController
 * @Description: 测试自定义MVC
 * @author: zhoujian
 * @date: 2021/3/5 13:29
 * @version: 1.0
 */
@Controller
@RequestMapping("/fuyi")
public class FuYiController {

    @ResponseBody
    @RequestMapping("/hello")
    public String hello(){
        return "hello fuyi";
    }

    @ResponseBody
    @RequestMapping(value = "/succ")
    public String success(){
        return "successful";
    }

}
