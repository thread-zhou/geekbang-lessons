package org.geektimes.configuration.spi;

import org.geektimes.configuration.ConfigurationInitializer;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * @ClassName: UserPlatformConfigurationServletContainerInitializer
 * @Description: 在 Listener、Servlet、Filter 初始化之前初始化 UserPlatformConfiguration
 *
 * @see ConfigurationInitializer
 *
 * 参考:
 * @see SpringServletContainerInitializer
 *
 * @author: zhoujian
 * @date: 2021/3/21 21:40
 * @version: 1.0
 */
@HandlesTypes(ConfigurationInitializer.class)
public class UserPlatformConfigurationServletContainerInitializer implements ServletContainerInitializer {

    public UserPlatformConfigurationServletContainerInitializer(){}

    @Override
    public void onStartup(Set<Class<?>> userPlatformConfigurationInitializerClasses, ServletContext servletContext) throws ServletException {
        List<ConfigurationInitializer> initializers = new LinkedList<>();

        Iterator initializerIterator;
        if (userPlatformConfigurationInitializerClasses != null){
            initializerIterator = userPlatformConfigurationInitializerClasses.iterator();

            while (initializerIterator.hasNext()){
                Class initializerClass = (Class) initializerIterator.next();
                if (!initializerClass.isInterface() && !Modifier.isAbstract(initializerClass.getModifiers())
                        && ConfigurationInitializer.class.isAssignableFrom(initializerClass)) {
                    try {
                        initializers.add((ConfigurationInitializer) initializerClass.newInstance());
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            for (ConfigurationInitializer configurationInitializer : ServiceLoader.load(ConfigurationInitializer.class, servletContext.getClassLoader())) {
                initializers.add(configurationInitializer);
            }
        }
        if (initializers.isEmpty()){
            servletContext.log("No UserPlatformConfigurationInitializer types detected on classpath");
        }else {
            servletContext.log(initializers.size() + " UserPlatformConfigurationInitializer detected on classpath");
            // TODO: 2021/3/21 支持排序
            initializerIterator = initializers.iterator();
            while (initializerIterator.hasNext()) {
                ConfigurationInitializer initializer = (ConfigurationInitializer) initializerIterator.next();
                initializer.onStartup(servletContext);
            }
        }
    }
}
