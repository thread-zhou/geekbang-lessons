# geekbang-lessons

极客时间课程工程

## 如何启动

- git clone https://github.com/thread-zhou/geekbang-lessons.git

- cd geekbang-lessons/projects/stage-0/user-platform (进入 \user-platform 目录)

- mvn clean package

- java -jar user-web\target\user-web-v1-SNAPSHOT.jar

## JMX 示例

> 在项目启动后，可进行以下链接的访问，获取到监控信息, 这里简单的展示了 ComponentContext 注册的内容（暂不支持内部详细信息的展示）
> 
> `MBean` 通过 `org.geektimes.manager.listener.MBeanRegisterListener` 进行注册，`ObjectName` = `org.geektimes.manager.mbean.context:type=ComponentContext`

- 列举 `org.geektimes.manager.mbean.context` 中的内容: [http://localhost:9090/jolokia/list/org.geektimes.manager.mbean.context](http://localhost:9090/jolokia/list/org.geektimes.manager.mbean.context)
  
- 读取 `bean/UserService` 中的内容: [http://localhost:9090/jolokia/read/org.geektimes.manager.mbean.context:type=ComponentContext/bean!/UserService](http://localhost:9090/jolokia/read/org.geektimes.manager.mbean.context:type=ComponentContext/bean!/UserService)

## 配置集成

> 目前仅进行配置源的简单集成，并未与系统进行细致的集成，通过 `org.geektimes.projects.user.web.listener.TestInitializerListener` 打印检测
> 
> 执行命令: java -Dapplication.name="User-Web Client V1.0" -jar user-web\target\user-web-v1-SNAPSHOT.jar
> 
> 打印内容: JNDI -> 信息: JNDI Env [property/ApplicationName] is [User-Web-1.0.0]
> 打印内容: -Dxx -> 信息: System Env [application.name] is [User-Web Client V1.0]

### 已集成数据源

- `org.geektimes.configuration.spi.source.SystemEnvConfigSource`
- `org.geektimes.configuration.spi.source.SystemPropertiesConfigSource`
- `org.geektimes.web.configuration.spi.source.JndiConfigSource`
