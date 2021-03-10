package org.geektimes.web;

import lombok.Builder;
import lombok.Getter;

/**
 * @ClassName: Configuration
 * @Description: 服务器相关配置
 * @author: zhoujian
 * @date: 2021/3/5 12:58
 * @version: 1.0
 */
@Builder
@Getter
public class Configuration {

    /**
     * 启动类
     */
    private Class<?> bootClass;

    /**
     * 资源目录
     */
    @Builder.Default
    private String resourcePath = "src/main/resources/";

    /**
     * jsp目录
     */
    @Builder.Default
    private String viewPath = "/templates/";

    /**
     * 静态文件目录
     */
    @Builder.Default
    private String assetPath = "/static/";

    /**
     * 端口号
     */
    @Builder.Default
    private int serverPort = 9090;

    /**
     * tomcat docBase目录
     */
    @Builder.Default
    private String docBase = "";

    /**
     * tomcat contextPath目录
     */
    @Builder.Default
    private String contextPath = "";

    @Builder.Default
    private boolean naming = true;
}
