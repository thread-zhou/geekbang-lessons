package org.geektimes.boot;

import org.geektimes.boot.wrapper.PrioritizedWrapper;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @ClassName: UserPlatformServletContainerInitializer
 * @Description: UserPlatform 初始化器, 触发时机: 早于Listener、Filter、Servlet初始化
 *
 * 实现于 {@link ServletContainerInitializer}, 参照 Spring Framework中 <code>SpringServletContainerInitializer</code>
 *
 * @author: zhoujian
 * @date: 2021/3/23 21:00
 * @version: 1.0
 */
@HandlesTypes(ApplicationBootstrapInitializer.class)
public class UserPlatformServletContainerInitializer implements ServletContainerInitializer {

    public UserPlatformServletContainerInitializer(){}

    @Override
    public void onStartup(Set<Class<?>> bootInitializerClassSet, ServletContext servletContext) throws ServletException {
        List<PrioritizedWrapper<ApplicationBootstrapInitializer>> initializers = new LinkedList<>();

        Iterator initializerIterator;
        if (bootInitializerClassSet != null && bootInitializerClassSet.size() > 0){
            initializerIterator = bootInitializerClassSet.iterator();

            while (initializerIterator.hasNext()){
                Class initializerClass = (Class) initializerIterator.next();
                if (!initializerClass.isInterface() && !Modifier.isAbstract(initializerClass.getModifiers())
                        && ApplicationBootstrapInitializer.class.isAssignableFrom(initializerClass)) {
                    try {
                        ApplicationBootstrapInitializer initializer = (ApplicationBootstrapInitializer) initializerClass.newInstance();
                        initializers.add(new PrioritizedWrapper<ApplicationBootstrapInitializer>(initializer, initializer.getPriority(), initializer.getClass().getSimpleName()));
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            for (ApplicationBootstrapInitializer bootInitializer : ServiceLoader.load(ApplicationBootstrapInitializer.class, servletContext.getClassLoader())) {
                initializers.add(new PrioritizedWrapper<ApplicationBootstrapInitializer>(bootInitializer, bootInitializer.getPriority(), bootInitializer.getClass().getSimpleName()));
            }
        }
        if (initializers.isEmpty()){
            servletContext.log("No ApplicationBootstrapInitializer types detected on classpath");
        }else {
            servletContext.log(initializers.size() + " ApplicationBootstrapInitializer detected on classpath");
            Collections.sort(initializers);
            initializerIterator = initializers.iterator();
            while (initializerIterator.hasNext()) {
                ApplicationBootstrapInitializer initializer = ((PrioritizedWrapper<ApplicationBootstrapInitializer>) initializerIterator.next()).getWrapped();
                initializer.onStartup(servletContext);
            }
        }
    }
}
