package org.geektimes.manager;

import org.geektimes.boot.ApplicationBootstrapInitializer;
import org.geektimes.manager.listener.MBeanRegisterListener;
import org.jolokia.http.AgentServlet;

import javax.servlet.Registration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

/**
 * @ClassName: ManagerBootstrapInitializer
 * @Description: Java Manager 初始化器
 * @author: zhoujian
 * @date: 2021/3/24 19:26
 * @version: 1.0
 */
public class ManagerBootstrapInitializer implements ApplicationBootstrapInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        servletContext.addListener(MBeanRegisterListener.class);
        ServletRegistration.Dynamic agentServlet = servletContext.addServlet("AgentServlet", AgentServlet.class);
        agentServlet.setLoadOnStartup(1);
        agentServlet.addMapping("/jolokia/*");
    }

    @Override
    public int getPriority() {
        return 666;
    }
}
