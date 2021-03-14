package org.geektimes.web.core.context.provider;

import javax.servlet.ServletContext;

/**
 * @InterfaceName: ComponentContextProvider
 * @Description: 组件上下文提供器 负责为组件上下文提供组件支持
 * @author: zhoujian
 * @date: 2021/3/11 13:04
 * @version: 1.0
 */
public interface ComponentContextProvider {

    /**
     * 组件提供器启动入口
     * @author zhoujian
     * @date 13:06 2021/3/11
     * @param servletContext
     * @return void
     **/
    void provide(ServletContext servletContext) throws RuntimeException;

}
