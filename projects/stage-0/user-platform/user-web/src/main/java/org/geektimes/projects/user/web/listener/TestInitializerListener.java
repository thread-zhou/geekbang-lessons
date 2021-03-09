package org.geektimes.projects.user.web.listener;

import org.geektimes.projects.user.sql.DBConnectionManager;
import org.geektimes.web.core.ComponentContext;
import org.geektimes.web.core.ComponentContextFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * 测试类
 * @author zhoujian
 * @date 21:04 2021/3/9
 * @param 
 * @return 
 **/
@Deprecated
public class TestInitializerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ComponentContext context = ComponentContextFactory.getComponentContext();
        DBConnectionManager dbConnectionManager = context.getComponent("bean/DBConnectionManager");
        dbConnectionManager.getConnection();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
