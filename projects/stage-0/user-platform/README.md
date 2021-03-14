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