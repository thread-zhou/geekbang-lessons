package org.geektimes.projects.user.web.listener;

import org.eclipse.microprofile.config.Config;
import org.geektimes.configuration.ConfigurationBootstrapInitializer;
import org.geektimes.projects.user.sql.DBConnectionManager;
import org.geektimes.web.core.ComponentContext;
import org.geektimes.web.core.ComponentContextFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.logging.Logger;

/**
 * 测试类
 * @author zhoujian
 * @date 21:04 2021/3/9
 * @param 
 * @return 
 **/
@Deprecated
public class TestInitializerListener implements ServletContextListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ComponentContext context = ComponentContextFactory.getComponentContext();
        DBConnectionManager dbConnectionManager = context.getComponent("bean/DBConnectionManager");
        dbConnectionManager.getConnection();
        dbConnectionManager.getEntityManager();
        logger.info("所有的 JNDI 组件名称：[");
        context.getComponentNames().forEach(logger::info);
        logger.info("]");

        Config config = (Config) sce.getServletContext().getAttribute(ConfigurationBootstrapInitializer.CONFIG);
        if (config != null) {
            logger.info("JNDI Env [property/ApplicationName] is [" + config
            .getValue("property/ApplicationName", String.class) + "]");

            logger.info("System Env [application.name] is [" + config
                    .getValue("application.name", String.class) + "]");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
