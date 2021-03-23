package org.geektimes.boot;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @InterfaceName: ApplicationBootstrapInitializer
 * @Description: 程序初始化引导接口
 *
 * 用于自定义程序初始化引导行为
 *
 * @author: zhoujian
 * @date: 2021/3/23 21:04
 * @version: 1.0
 */
public interface ApplicationBootstrapInitializer {

    int DEFAULT_PRIORITY = 100;

    /**
     * 初始化方法
     * @author zhoujian
     * @date 21:30 2021/3/23
     * @param servletContext
     * @return void
     **/
    void onStartup(ServletContext servletContext) throws ServletException;

    /**
     * 获取权值, 默认为 100
     * @author zhoujian
     * @date 21:31 2021/3/23
     * @param
     * @return int
     **/
    default int getPriority() {
        return DEFAULT_PRIORITY;
    }
}
