package org.geektimes.web.core.context;

import org.geektimes.web.core.AbstractComponentContext;
import org.geektimes.web.core.ComponentContextFactory;

import javax.servlet.ServletContext;

/**
 * @ClassName: JndiComponentContext
 * @Description: JDNI 组件上下文
 * @author: zhoujian
 * @date: 2021/3/9 20:46
 * @version: 1.0
 */
final class JndiComponentContext extends AbstractComponentContext {

    public static final String JNDI_COMPONENT_CONTEXT_NAME = JndiComponentContext.class.getName();

    @Override
    protected void additionalInit(ServletContext servletContext) throws RuntimeException {
        servletContext.setAttribute(COMPONENT_CONTEXT_NAME, this);
        ComponentContextFactory.setServletContext(servletContext);
    }
}
