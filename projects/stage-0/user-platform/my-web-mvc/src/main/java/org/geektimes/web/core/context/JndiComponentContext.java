package org.geektimes.web.core.context;

import org.geektimes.web.core.AbstractComponentContext;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.NoSuchElementException;

/**
 * @ClassName: JndiComponentContext
 * @Description: JDNI 组件上下文
 * @author: zhoujian
 * @date: 2021/3/9 20:46
 * @version: 1.0
 */
public class JndiComponentContext extends AbstractComponentContext {

    public static final String JNDI_COMPONENT_CONTEXT_NAME = JndiComponentContext.class.getName();
    private static final String COMPONENT_ENV_CONTEXT_NAME = "java:comp/env";

    private Context context;


    @Override
    protected void genericInit() throws RuntimeException {
        try {
            this.context = (Context) new InitialContext().lookup(COMPONENT_ENV_CONTEXT_NAME);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <C> C getComponent(String name) {
        C component = null;
        try {
            component = (C) this.context.lookup(name);
        } catch (NamingException e) {
            throw new NoSuchElementException(name);
        }
        return component;
    }

    @Override
    public void destroy() throws RuntimeException {
        if (this.context != null){
            try {
                this.context.close();
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
