package com.geektimes.web.ioc;

import com.geektimes.web.bean.FuYiController;
import lombok.extern.slf4j.Slf4j;
import org.geektimes.web.core.BeanContainer;
import org.geektimes.web.ioc.Ioc;
import org.junit.Test;

/**
 * @ClassName: IocTest
 * @Description: TODO
 * @author: zhoujian
 * @date: 2021/3/2 22:41
 * @version: 1.0
 */
@Slf4j
public class IocTest {

    @Test
    public void test() {
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("com.geektimes.web");
        new Ioc().doIoc();
        FuYiController controller = (FuYiController) beanContainer.getBean(FuYiController.class);
        controller.hello();
    }
}
