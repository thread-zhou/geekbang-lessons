package org.geektimes.configuration.spi.source.servlet;

import org.geektimes.configuration.spi.source.MapBasedConfigSource;

import javax.servlet.ServletContext;
import java.util.Enumeration;
import java.util.Map;

/**
 * @ClassName: ServletContextConfigSource
 * @Description: {@link org.eclipse.microprofile.config.spi.ConfigSource} 的 ServletContext 配置实现
 *
 * 获取{@link javax.servlet.ServletContext}初始化参数, 作为 {@link org.eclipse.microprofile.config.spi.ConfigSource}
 * 的一个组成部分
 *
 * @author: zhoujian
 * @date: 2021/3/21 21:02
 * @version: 1.0
 */
public class ServletContextConfigSource extends MapBasedConfigSource {

    private ServletContext servletContext;

    public ServletContextConfigSource(ServletContext servletContext) {
        super("ServletContext Init Parameters", 500, servletContext);
    }

    @Override
    protected void prepareConfig(Object[] args) throws Throwable {
        this.servletContext = (ServletContext) args[0];
    }

    @Override
    protected void doConfigData(Map configData) throws Throwable {
        Enumeration<String> parameterNames = servletContext.getInitParameterNames();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            configData.put(parameterName, servletContext.getInitParameter(parameterName));
        }
    }
}
