package org.geektimes.web.core;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * @InterfaceName: ComponentContext
 * @Description: 组件上下文
 * @author: zhoujian
 * @date: 2021/3/9 20:50
 * @version: 1.0
 */
public interface ComponentContext {

    String COMPONENT_CONTEXT_NAME = ComponentContext.class.getName();

    /**
     * 初始化
     * @author zhoujian
     * @date 20:54 2021/3/9
     * @param
     * @return void
     **/
    void init(ServletContext servletContext) throws RuntimeException;

    /**
     * 获取 {@link ServletContext}
     * @author zhoujian
     * @date 22:08 2021/3/10
     * @param
     * @return javax.servlet.ServletContext
     **/
    ServletContext getServletContext();

    /**
     * 通过组件标识符进行依赖查找
     * @author zhoujian
     * @date 20:54 2021/3/9
     * @param name 组件标识符
     * @return C
     **/
    <C> C getComponent(String name);

    void setComponent(String name, Object c);

    /**
     * 获取所有的组件名称
     * @author zhoujian
     * @date 23:35 2021/3/10
     * @param
     * @return java.util.List<java.lang.String>
     **/
    List<String> getComponentNames();

    /**
     * 通过Class获取组件
     * @author zhoujian
     * @date 23:58 2021/3/10
     * @param
     * @return java.util.List<C>
     **/
    <C> List<C> getComponentsBySuperClass(Class<C> c);

    /**
     * 销毁方法
     * @author zhoujian
     * @date 20:56 2021/3/9
     * @param
     * @return void
     **/
    void destroy() throws RuntimeException;

}
