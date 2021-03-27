# 第四周作业

## 内容

### 完善 my dependency-injection 模块

- 脱离 web.xml 配置实现 ComponentContext 自动初始化 
  
- 使用独立模块并且能够在 user-web 中运行成功

### 完善 my-configuration 模块

- Config 对象如何能被 my-web-mvc 使用
  
- 可能在 ServletContext 获取如何通过 ThreadLocal 获取

## 完成情况

- [x] 脱离 web.xml 配置实现 ComponentContext 自动初始化

1. 通过对 `ServletContainerInitializer` 进行拓展，便于自定义加载方式、时机、循序。由于我是用的是嵌入式 `Tomcat`，其最终打包格式为 `Jar`，所以无法完全支持 `ServletContainerInitializer` 规范，
所以在这里通过 `SPI` 的方式作为例外支持。（嵌入式 `Tomcat` 已测试，可正常运行；外置 `Tomcat` 未测试）
```java
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
```
2. 定义 `PrioritizedWrapper` 进行排序支持

```java
package org.geektimes.boot.wrapper;


/**
 * @ClassName: PrioritizedWrapper
 * @Description: 支持排序的包装类
 *
 * 默认权重为: 权重越高则优先级越高, 按照整数从大到小排序
 *
 * @author: zhoujian
 * @date: 2021/3/23 21:07
 * @version: 1.0
 */
public class PrioritizedWrapper<W> implements Comparable<PrioritizedWrapper<W>> {

    /**
     * 被包装的对象
     **/
    private final W wrapped;

    /**
     * 排序权重
     **/
    private final int priority;

    /**
     * 被包装对象的名称
     **/
    private final String name;

    public PrioritizedWrapper(W wrapped, int priority, String name){
        this.wrapped = wrapped;
        this.priority = priority;
        this.name = name;
    }

    public int getPriority() {
        return priority;
    }

    public W getWrapped() {
        return wrapped;
    }

    public String getName() {
        return name;
    }

    /**
     * 内置的比较器, 权重越高越优先
     * @author zhoujian
     * @date 21:13 2021/3/23
     * @param o
     * @return int
     **/
    @Override
    public int compareTo(PrioritizedWrapper<W> o) {
        return Integer.compare(o.getPriority(), this.getPriority());
    }
}

```

3. 在这里进行了 `ComponentContext` 的初始化

```java
package org.geektimes.projects.user.web.listener;

import org.geektimes.boot.ApplicationBootstrapInitializer;
import org.geektimes.configuration.ConfigurationBootstrapInitializer;
import org.geektimes.configuration.spi.UserPlatformConfigProviderResolver;
import org.geektimes.projects.user.spi.configuration.source.JndiConfigSource;
import org.geektimes.web.core.ComponentContext;
import org.geektimes.web.core.context.DefaultComponentContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

/**
 * @ClassName: ComponentContextInitializerListener
 * @Description: {@link ComponentContext} 初始化器
 *
 * 类似于 Spring#ContextLoaderListener
 *
 * @author zhoujian
 * @date: 2021/3/23 21:44
 * @version: 1.0
 */
public class ComponentContextBootstrapInitializer implements ApplicationBootstrapInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        ComponentContext componentContext = new DefaultComponentContext();
        componentContext.init(servletContext);
    }

    @Override
    public int getPriority() {
        return 777;
    }
}

```
--- 

- [x] 使用独立模块并且能够在 `user-web` 中运行成功

1. `my dependency-injection` 对应着我这里的 `user-core` 模块，已完成在 `user-web` 中使用

```text
<dependency>
    <groupId>org.geekbang.projects</groupId>
    <artifactId>user-core</artifactId>
    <version>${revision}</version>
</dependency>
```

--- 

- [x] Config 对象如何能被 `my-web-mvc` 使用

1. 这里可以使用如下的几种方式进行

    1. 通过 `ServletContext` 进行共享
        ```text
        servletContext.setAttribute(CONFIG, ConfigProvider.getConfig(servletContext.getClassLoader()));
        ```
    2. 通过 `org.eclipse.microprofile.config.ConfigProvider` 进行获取 --> `org.eclipse.microprofile.config.Config`
       ```text
        ConfigProvider.getConfig(servletContext.getClassLoader());
       ```
    3. 通过 `org.eclipse.microprofile.config.spi.ConfigProviderResolver` 进行获取 --> `org.eclipse.microprofile.config.spi.ConfigProviderResolver`
        ```text
         ConfigProviderResolver.instance();
        ```

--- 

- [x] 通过 ThreadLocal 获取

```java
package org.geektimes.configuration.context;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.spi.ConfigBuilder;
import org.geektimes.configuration.ConfigurationBootstrapInitializer;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;

/**
 * @ClassName: ConfigurationContext
 * @Description: 使用 {@link ThreadLocal} 进行 {@link org.eclipse.microprofile.config.Config}
 * 与 {@link org.eclipse.microprofile.config.spi.ConfigBuilder} 的持有
 *
 * 通过实现 {@link ServletRequestListener}接口，从而达到为每一个请求复制一个线程内共享的{@link Config}
 * 与{@link ConfigBuilder}
 *
 * 这里 {@link ConfigurationContext} 和 {@link javax.servlet.ServletContext} 中持有的区别，其实并没有本质上的区别，
 * 后者需先获取到 {@link javax.servlet.ServletContext} 对象，而后通过属性获取并强制类型转换；
 * 前者其实也是通过 {@link javax.servlet.ServletContext} 进行获取，但是通过 {@link ThreadLocal} 静态化，可以在每一个
 * 请求中通过静态方法获取；
 *
 * 当然，也可以直接通过 {@link org.eclipse.microprofile.config.ConfigProvider#getConfig(ClassLoader)}、
 * {@link ConfigProvider#getConfig()} 或 {@link org.eclipse.microprofile.config.spi.ConfigProviderResolver#instance()}
 * 进行对应句柄的获取，这里的重点在于对{@link ThreadLocal}使用的学习
 *
 * @author: zhoujian
 * @date: 2021/3/24 23:09
 * @version: 1.0
 */
public class ConfigurationContext implements ServletRequestListener {

    private static final ThreadLocal<Config> CONFIG_THREAD_LOCAL = new ThreadLocal<>();

    private static final ThreadLocal<ConfigBuilder> CONFIG_BUILDER_THREAD_LOCAL = new ThreadLocal<>();

    public static Config getConfig(){
        return CONFIG_THREAD_LOCAL.get();
    }

    public static ConfigBuilder getConfigBuilder(){
        return CONFIG_BUILDER_THREAD_LOCAL.get();
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        CONFIG_THREAD_LOCAL.set((Config) servletRequestEvent.getServletContext().getAttribute(ConfigurationBootstrapInitializer.CONFIG));
        CONFIG_BUILDER_THREAD_LOCAL.set((ConfigBuilder) servletRequestEvent.getServletContext().getAttribute(ConfigurationBootstrapInitializer.CONFIG_BUILDER));
    }

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        CONFIG_THREAD_LOCAL.remove();
        CONFIG_BUILDER_THREAD_LOCAL.remove();
    }
}


```
