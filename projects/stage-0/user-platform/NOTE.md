# geekbang-lessons

极客时间课程工程

## JNDI

> JNDI：（Java Naming and Directory Interface）号称依赖查找的一个工具

- jndi是延迟初始化（并未马上进行初始化），而是在lookup时进行初始化，这里可以考虑缓存

## ClassLoader


### 待整理：

1. `JDNI`
    - `JDNI` 是什么
    - `JDNI` 使用
2. 什么是 `Checked` 异常、什么是 `NoCheck` 异常

3. `ClassLoader`
    - `ClassLoader` 是什么
    - `ClassLoader` 与 `Class` 的关系
    
4. `SPI`
   - `SPI` 是什么
   - 为什么在 `Idea` 中启动时可以正常的读取到 `user-configuration` 与 `user-core` 模块下的 `org.eclipse.microprofile.config.spi.ConfigSource` 配置，
     但是通过命令行的方式启时（`java -Dapplication.name="User-Web Client" -jar user-web\target\user-web-v1-SNAPSHOT.jar`），仅能读取到 `user-core` 模块下的 `org.eclipse.microprofile.config.spi.ConfigSource` 配置 ?
     
      > 打包问题，参见:
      > 
      > - https://blog.csdn.net/ren78min/article/details/84095336
      > - http://maven.apache.org/plugins/maven-shade-plugin/examples/resource-transformers.html
     

## 相关技术

- 假设一个 Tomcat JVM 进程，三个 Web Apps，会不会相互冲突？（不会冲突）

- static 字段是 JVM 缓存吗？（是 ClassLoader 缓存）

- 命令行启动debug: java -Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=y -jar user-web\target\user-web-v1-SNAPSHOT.jar