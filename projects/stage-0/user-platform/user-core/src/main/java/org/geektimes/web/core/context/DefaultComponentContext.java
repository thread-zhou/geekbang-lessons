package org.geektimes.web.core.context;

import org.geektimes.web.core.AbstractComponentContext;
import org.geektimes.web.core.ComponentContextFactory;

import javax.servlet.ServletContext;

/**
 * @ClassName: DefaultComponentContext
 * @Description: 用于构造 {@link org.geektimes.web.core.ComponentContext}
 * @author: zhoujian
 * @date: 2021/3/11 13:11
 * @version: 1.0
 */
public class DefaultComponentContext extends AbstractComponentContext {

    @Override
    protected void additionalInit(ServletContext servletContext) throws RuntimeException {
        servletContext.setAttribute(COMPONENT_CONTEXT_NAME, this);
        ComponentContextFactory.setServletContext(servletContext);
    }
}
