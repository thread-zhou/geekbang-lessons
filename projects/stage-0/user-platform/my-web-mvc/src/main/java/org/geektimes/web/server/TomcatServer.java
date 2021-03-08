package org.geektimes.web.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.geektimes.web.Configuration;
import org.geektimes.web.FuYi;
import org.geektimes.web.mvc.DispatcherServlet;

import javax.servlet.ServletContext;
import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;

/**
 * @ClassName: TomcatServer
 * @Description: Tomcat 服务器
 *
 * 这个类主要就是配置tomcat，和配置普通的外部tomcat有点类似只是这里是用代码的方式。
 * 注意的是在getRootFolder()方法中获取的是当前项目目录下的target文件夹，
 * 即idea默认的编译文件保存的位置，如果修改了编译文件保存位置，这里也要修改。
 *
 * @author: zhoujian
 * @date: 2021/3/5 13:01
 * @version: 1.0
 */
@Slf4j
public class TomcatServer implements Server{

    private Tomcat tomcat;

    private ServletContext servletContext;

    public TomcatServer() {
        new TomcatServer(FuYi.getConfiguration());
    }

    public TomcatServer(Configuration configuration) {
        try {
            this.tomcat = new Tomcat();
            tomcat.setPort(configuration.getServerPort());
            // 启动JNDI支持
            tomcat.enableNaming();
            File root = getRootFolder(configuration.getBootClass());
            File webContentFolder = new File(root.getAbsolutePath(), configuration.getResourcePath());
            if (!webContentFolder.exists()) {
                webContentFolder = Files.createTempDirectory("default-doc-base").toFile();
            }
            log.info("Tomcat:configuring app with docBase: [{}]", webContentFolder.getAbsolutePath());
            tomcat.setBaseDir(webContentFolder.getAbsolutePath());
            StandardContext ctx = (StandardContext) tomcat.addWebapp(configuration.getContextPath(), webContentFolder.getAbsolutePath());
            ctx.setParentClassLoader(this.getClass().getClassLoader());
            // 添加jspServlet，defaultServlet和自己实现的dispatcherServlet
            tomcat.addServlet("", "dispatcherServlet", new DispatcherServlet()).setLoadOnStartup(0);
            ctx.addServletMapping("/*", "dispatcherServlet");
            this.servletContext = ctx.getServletContext();

        } catch (Exception e) {
            log.error("初始化Tomcat失败", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void startServer() throws Exception {
        tomcat.start();
        String address = tomcat.getServer().getAddress();
        int port = tomcat.getConnector().getPort();
        log.info("local address: http://{}:{}", address, port);
        tomcat.getServer().await();
    }

    @Override
    public void stopServer() throws Exception {
        tomcat.stop();
    }

    @Override
    public ServletContext getServletContext() {
        return this.servletContext;
    }

    private File getRootFolder(Class bootClass) {
        try {
            File root;
            String runningJarPath = bootClass.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceAll("\\\\", "/");
            int lastIndexOf = runningJarPath.lastIndexOf("/target/");
            if (lastIndexOf < 0) {
                root = new File("");
            } else {
                root = new File(runningJarPath.substring(0, lastIndexOf));
            }
            log.info("Tomcat:application resolved root folder: [{}]", root.getAbsolutePath());
            return root;
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
}
