# 第三周作业

## 内容

### 需求一（必须）

- 整合 https://jolokia.org/，实现一个自定义 JMX MBean，通过 Jolokia 做 Servlet 代理

### 需求二（选做）

- 继续完成 Microprofile config API 中的实现
  
    - 扩展 org.eclipse.microprofile.config.spi.ConfigSource 实现，包括 OS 环境变量，以及本地配置文件
    - 扩展 org.eclipse.microprofile.config.spi.Converter 实现，提供 String 类型到简单类型

- 通过 org.eclipse.microprofile.config.Config 读取当前应用名称

    - 应用名称 property name = “application.name”

## 完成情况

- [x] 整合 https://jolokia.org/，实现一个自定义 JMX MBean，通过 Jolokia 做 Servlet 代理

> 在项目启动后，可进行以下链接的访问，获取到监控信息, 这里简单的展示了 ComponentContext 注册的内容（暂不支持内部详细信息的展示）
>
> `MBean` 通过 `org.geektimes.manager.listener.MBeanRegisterListener` 进行注册，`ObjectName` = `org.geektimes.manager.mbean.context:type=ComponentContext`

- 列举 `org.geektimes.manager.mbean.context` 中的内容: [http://localhost:9090/jolokia/list/org.geektimes.manager.mbean.context](http://localhost:9090/jolokia/list/org.geektimes.manager.mbean.context)

- 读取 `bean/UserService` 中的内容: [http://localhost:9090/jolokia/read/org.geektimes.manager.mbean.context:type=ComponentContext/bean!/UserService](http://localhost:9090/jolokia/read/org.geektimes.manager.mbean.context:type=ComponentContext/bean!/UserService)  

---

- [x] 扩展 org.eclipse.microprofile.config.spi.ConfigSource 实现，包括 OS 环境变量，以及本地配置文件

> 目前仅进行配置源的简单集成，并未与系统进行细致的集成，通过 `org.geektimes.projects.user.web.listener.TestInitializerListener` 打印检测

#### 已集成数据源

- `org.geektimes.configuration.spi.source.SystemEnvConfigSource`
- `org.geektimes.configuration.spi.source.SystemPropertiesConfigSource`
- `org.geektimes.web.configuration.spi.source.JndiConfigSource`
- `org.geektimes.configuration.spi.source.MicroprofileResourceConfigSource`
- `org.geektimes.configuration.spi.source.servlet.ServletContextConfigSource`

---

- [x] 扩展 org.eclipse.microprofile.config.spi.Converter 实现，提供 String 类型到简单类型

---

- [x] 通过 org.eclipse.microprofile.config.Config 读取当前应用名称，应用名称 property name = “application.name”

> 执行命令: java -Dapplication.name="User-Web Client V1.0" -jar user-web\target\user-web-v1-SNAPSHOT.jar
>
> 打印内容: JNDI -> 信息: JNDI Env [property/ApplicationName] is [User-Web-1.0.0]
> 打印内容: -Dxx -> 信息: System Env [application.name] is [User-Web Client V1.0]