package org.geektimes.projects.user.spi.configuration.source;

import org.eclipse.microprofile.config.spi.ConfigSource;
import org.geektimes.web.core.ComponentContext;
import org.geektimes.web.core.ComponentContextFactory;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @ClassName: JndiConfigSource
 * @Description: JNDI 配置源
 * @author: zhoujian
 * @date: 2021/3/14 22:17
 * @version: 1.0
 */
public class JndiConfigSource implements ConfigSource {

    private ComponentContext context;

    public JndiConfigSource(){
        ComponentContext globalContext = ComponentContextFactory.getComponentContext();
        if (globalContext == null){
            throw new RuntimeException("ComponentContextFactory.getComponentContext() is Null");
        }
        this.context = globalContext;
    }

    @Override
    public Set<String> getPropertyNames() {
        Set<String> propertiesNames = new LinkedHashSet<>(context.getComponentNames());
        return Collections.unmodifiableSet(propertiesNames);
    }

    @Override
    public String getValue(String s) {
        return context.getComponent(s);
    }

    @Override
    public String getName() {
        return "JNDI Properties";
    }
}
