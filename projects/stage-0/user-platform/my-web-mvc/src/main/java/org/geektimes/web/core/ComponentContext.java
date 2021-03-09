package org.geektimes.web.core;

import javax.servlet.ServletContext;

/**
 * @InterfaceName: ComponentContext
 * @Description: 组件上下文
 * @author: zhoujian
 * @date: 2021/3/9 20:50
 * @version: 1.0
 */
public interface ComponentContext {

    static final String COMPONENT_CONTEXT_NAME = ComponentContext.class.getName();

    /**
     * 初始化
     * @author zhoujian
     * @date 20:54 2021/3/9
     * @param
     * @return void
     **/
    void init() throws RuntimeException;

    /**
     * 通过组件标识符进行依赖查找
     * @author zhoujian
     * @date 20:54 2021/3/9
     * @param name 组件标识符
     * @return C
     **/
    <C> C getComponent(String name);

    /**
     * 销毁方法
     * @author zhoujian
     * @date 20:56 2021/3/9
     * @param
     * @return void
     **/
    void destroy() throws RuntimeException;

}
