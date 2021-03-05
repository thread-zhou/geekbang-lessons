package org.geektimes.web.server;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.jasper.servlet.JspServlet;
import org.geektimes.web.Configuration;
import org.geektimes.web.FuYi;
import org.geektimes.web.mvc.DispatcherServlet;

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

    public TomcatServer() {
        new TomcatServer(FuYi.getConfiguration());
    }

    public TomcatServer(Configuration configuration) {
        try {
            this.tomcat = new Tomcat();
            tomcat.setBaseDir(configuration.getDocBase());
            tomcat.setPort(configuration.getServerPort());

            File root = getRootFolder();
            File webContentFolder = new File(root.getAbsolutePath(), configuration.getResourcePath());
            if (!webContentFolder.exists()) {
                webContentFolder = Files.createTempDirectory("default-doc-base").toFile();
            }

            log.info("Tomcat:configuring app with basedir: [{}]", webContentFolder.getAbsolutePath());
            StandardContext ctx = (StandardContext) tomcat.addWebapp(configuration.getContextPath(), webContentFolder.getAbsolutePath());
            ctx.setParentClassLoader(this.getClass().getClassLoader());

            WebResourceRoot resources = new StandardRoot(ctx);
            ctx.setResources(resources);
            // 添加jspServlet，defaultServlet和自己实现的dispatcherServlet

            // 去除了JspHandler和SimpleUrlHandler这两个servlet的注册
            // LoadOnStartup，当这个值大于等于0时就会随tomcat启动也实例化
//             tomcat.addServlet("", "jspServlet", new JspServlet()).setLoadOnStartup(3);
            // 用于处理静态资源如css、js文件等
//            tomcat.addServlet("", "defaultServlet", new DefaultServlet()).setLoadOnStartup(1);
//            ctx.addServletMappingDecoded("/templates/" + "*", "jspServlet");
//            ctx.addServletMappingDecoded("/static/" + "*", "defaultServlet");
            tomcat.addServlet("", "dispatcherServlet", new DispatcherServlet()).setLoadOnStartup(0);

            ctx.addServletMappingDecoded("/*", "dispatcherServlet");
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

    private File getRootFolder() {
        try {
            File root;
            String runningJarPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceAll("\\\\", "/");
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
