package org.geektimes.configuration;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @InterfaceName: UserPlatformConfigurationInitializer
 * @Description: 用于初始化 UserPlatformConfiguration
 * @author: zhoujian
 * @date: 2021/3/21 21:48
 * @version: 1.0
 */
public interface ConfigurationInitializer {

    void onStartup(ServletContext servletContext) throws ServletException;

}
