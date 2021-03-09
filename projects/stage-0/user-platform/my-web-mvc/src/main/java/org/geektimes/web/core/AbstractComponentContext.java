package org.geektimes.web.core;

import org.geektimes.web.core.ComponentContext;

import javax.servlet.ServletContext;

/**
 * @ClassName: AbstractComponentContext
 * @Description: {@link ComponentContext} 基础实现
 * @author: zhoujian
 * @date: 2021/3/9 21:24
 * @version: 1.0
 */
public abstract class AbstractComponentContext implements ComponentContext {

    @Override
    public void init() throws RuntimeException {
        genericInit();
    }

    protected void genericInit () throws RuntimeException {};
}
