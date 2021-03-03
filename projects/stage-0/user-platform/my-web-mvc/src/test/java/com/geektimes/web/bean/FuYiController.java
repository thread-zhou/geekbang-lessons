package com.geektimes.web.bean;

import lombok.extern.slf4j.Slf4j;
import org.geektimes.web.core.annotation.Autowired;
import org.geektimes.web.core.annotation.Controller;

/**
 * @ClassName: FuyiController
 * @Description: TODO
 * @author: zhoujian
 * @date: 2021/3/2 22:35
 * @version: 1.0
 */
@Slf4j
@Controller
public class FuYiController {

    @Autowired
    private FuYiService fuyiService;

    public void hello() {
        log.info(fuyiService.helloWorld());
    }

    public void helloForAspect() {
        log.info("Hello Aspectj");
    }
}
