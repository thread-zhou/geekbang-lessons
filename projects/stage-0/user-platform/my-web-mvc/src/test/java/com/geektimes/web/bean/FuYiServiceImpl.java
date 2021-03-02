package com.geektimes.web.bean;

import org.geektimes.web.core.annotation.Service;

/**
 * @ClassName: FuyiServiceImpl
 * @Description: TODO
 * @author: zhoujian
 * @date: 2021/3/2 22:36
 * @version: 1.0
 */
@Service
public class FuYiServiceImpl implements FuYiService {
    @Override
    public String helloWorld() {
        return "hello world";
    }
}
