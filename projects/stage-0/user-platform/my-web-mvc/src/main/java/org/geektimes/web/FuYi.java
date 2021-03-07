package org.geektimes.web;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.geektimes.web.aop.Aop;
import org.geektimes.web.core.BeanContainer;
import org.geektimes.web.ioc.Ioc;
import org.geektimes.web.server.Server;
import org.geektimes.web.server.TomcatServer;

import javax.servlet.ServletContext;

/**
 * @ClassName: FuYi
 * @Description: FuYi Starter
 *
 *
 * 这个类中有三个启动方法都会调用FuYi@start()方法，在这个方法里做了三件事：
 *
 * 读取configuration中的配置
 * BeanContainer扫描包并加载Bean
 * 执行Aop
 * 执行Ioc
 * 启动Tomcat服务器
 * 这里的执行是有顺序要求的，特别是Aop必须要在Ioc之前执行，不然注入到类中的属性都是没被代理的。
 *
 * @author: zhoujian
 * @date: 2021/3/5 13:13
 * @version: 1.0
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FuYi {

    /**
     * 全局配置
     */
    @Getter
    private static Configuration configuration = Configuration.builder().build();

    /**
     * 默认服务器
     */
    @Getter
    private static Server server;

    /**
     * ServletContext
     */
    @Getter
    private static ServletContext servletContext;

    /**
     * 启动
     */
    public static void run(Class<?> bootClass) {
        run(Configuration.builder().bootClass(bootClass).build());
    }

    /**
     * 启动
     */
    public static void run(Class<?> bootClass, int port) {
        run(Configuration.builder().bootClass(bootClass).serverPort(port).build());
    }

    /**
     * 启动
     */
    public static void run(Configuration configuration) {
        new FuYi().start(configuration);
    }

    /**
     * 初始化
     */
    private void start(Configuration configuration) {
        try {
            FuYi.configuration = configuration;
            String basePackage = configuration.getBootClass().getPackage().getName();
            BeanContainer.getInstance().loadBeans(basePackage);
            //注意Aop必须在Ioc之前执行
            new Aop().doAop();
            new Ioc().doIoc();

            server = new TomcatServer(configuration);
            servletContext = server.getServletContext();
            server.startServer();
        } catch (Exception e) {
            log.error("FuYi 启动失败", e);
        }
    }
}
