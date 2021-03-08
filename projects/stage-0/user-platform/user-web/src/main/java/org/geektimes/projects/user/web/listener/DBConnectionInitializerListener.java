package org.geektimes.projects.user.web.listener;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class DBConnectionInitializerListener implements ServletContextListener {

    private ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        this.servletContext = sce.getServletContext();
        Connection connection = getConnection();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }

    private Connection getConnection(){
        Context context = null;
        Connection connection =  null;

        try {
            context = new InitialContext();
            Context envContext = (Context) context.lookup("java:comp/env");

            // 依赖查找
            DataSource dataSource = (DataSource) envContext.lookup("jdbc/UserPlatformDB");
            connection = dataSource.getConnection();
        }catch (NamingException | SQLException exception) {
            servletContext.log(exception.getMessage(), exception);
        }
        if (connection != null) {
            servletContext.log("成功获取 JNDI 数据库连接");
        }
        return connection;
    }
}
