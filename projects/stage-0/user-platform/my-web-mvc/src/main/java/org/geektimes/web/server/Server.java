package org.geektimes.web.server;

import javax.servlet.ServletContext;

/**
 * @InterfaceName: Server
 * @Description: 服务器 interface
 * @author: zhoujian
 * @date: 2021/3/5 13:00
 * @version: 1.0
 */
public interface Server {

    /**
     * 启动服务器
     */
    void startServer() throws Exception;

    /**
     * 停止服务器
     */
    void stopServer() throws Exception;

    ServletContext getServletContext();
}
