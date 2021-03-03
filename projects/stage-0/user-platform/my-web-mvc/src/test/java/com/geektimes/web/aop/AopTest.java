package com.geektimes.web.aop;

import com.geektimes.web.bean.FuYiController;
import lombok.extern.slf4j.Slf4j;
import org.geektimes.web.aop.Aop;
import org.geektimes.web.core.BeanContainer;
import org.geektimes.web.ioc.Ioc;
import org.junit.Test;

/**
 * @ClassName: AopTest
 * @Description: TODO
 * @author: zhoujian
 * @date: 2021/3/3 13:34
 * @version: 1.0
 */
@Slf4j
public class AopTest {

    @Test
    public void doAop() {
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("com.geektimes.web");
        new Aop().doAop();
        new Ioc().doIoc();
        FuYiController controller = (FuYiController) beanContainer.getBean(FuYiController.class);
        controller.hello();
        controller.helloForAspect();
    }

    @Test
    public void doAdviceChainAop(){
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("com.geektimes.web");
        new Aop().doAop();
        new Ioc().doIoc();
        FuYiController controller = (FuYiController) beanContainer.getBean(FuYiController.class);
        controller.hello();
    }
}
