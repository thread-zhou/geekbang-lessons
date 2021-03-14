package org.geektimes.web.core;
import org.geektimes.web.core.context.provider.ComponentContextProvider;
import javax.servlet.ServletContext;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: AbstractComponentContext
 * @Description: {@link ComponentContext} 基础实现
 * @author: zhoujian
 * @date: 2021/3/9 21:24
 * @version: 1.0
 */
public abstract class AbstractComponentContext implements ComponentContext {

    private ServletContext servletContext;

    private static Map<String, Object> COMPONENT_MAP = new LinkedHashMap<>();


    @Override
    public void init(ServletContext servletContext) throws RuntimeException {
        this.servletContext = servletContext;
        additionalInit(servletContext);
        for (ComponentContextProvider provider : ServiceLoader.load(ComponentContextProvider.class)){
            provider.provide(servletContext);
        }
    }


    @Override
    public void setComponent(String name, Object c) {
        COMPONENT_MAP.put(name, c);
    }

    protected Map<String, Object> getComponentsMap(){
        return COMPONENT_MAP;
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }

    @Override
    public <C> C getComponent(String name) {
        return (C) COMPONENT_MAP.get(name);
    }

    @Override
    public <C> List<C> getComponentsBySuperClass(Class<C> c) {
        return COMPONENT_MAP.values().stream()
                .filter(component -> c.isAssignableFrom(component.getClass()))
                .map((component -> (C) component)).collect(Collectors.toList());
    }

    /**
     * 额外的处理
     * @author zhoujian
     * @date 13:56 2021/3/11
     * @param servletContext
     * @return void
     **/
    protected void additionalInit (ServletContext servletContext) throws RuntimeException {};


    /**
     * 获取所有的组件名称
     *
     * @return
     */
    public List<String> getComponentNames() {
        return new ArrayList<>(getComponentsMap().keySet());
    }

    @Override
    public void destroy() throws RuntimeException {
    }
}
