package org.geektimes.manager.listener;

import org.geektimes.manager.mbean.context.ComponentContextMBean;
import org.geektimes.web.core.ComponentContextFactory;

import javax.management.*;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.lang.management.ManagementFactory;

/**
 * @ClassName: MBeanRegisterListener
 * @Description: 用于注册 MBean
 * @author: zhoujian
 * @date: 2021/3/14 17:29
 * @version: 1.0
 */
public class MBeanRegisterListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName objectName = new ObjectName("org.geektimes.manager.mbean.context:type=ComponentContext");
            ComponentContextMBean componentContextMBean = new ComponentContextMBean(ComponentContextFactory.getComponentContext());
            mBeanServer.registerMBean(componentContextMBean, objectName);
        } catch (MalformedObjectNameException e) {
            e.printStackTrace();
        } catch (NotCompliantMBeanException e) {
            e.printStackTrace();
        } catch (InstanceAlreadyExistsException e) {
            e.printStackTrace();
        } catch (MBeanRegistrationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
