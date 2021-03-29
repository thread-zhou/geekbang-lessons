# 学习笔记

## 待整理：

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
     
## JNDI

> JNDI：（Java Naming and Directory Interface）号称依赖查找的一个工具

- jndi是延迟初始化（并未马上进行初始化），而是在lookup时进行初始化，这里可以考虑缓存

## ClassLoader

## ThreadLocal

This class provides thread-local variables. These variables differ from their normal counterparts in that each thread that accesses one (via its get or set method) has its own, independently initialized copy of the variable. ThreadLocal instances are typically private static fields in classes that wish to associate state with a thread (e.g., a user ID or Transaction ID).

这个类提供线程局部变量。这些变量与普通变量的不同之处在于，每个访问这些变量的线程(通过其get或set方法)都有自己独立初始化的变量副本。ThreadLocal实例通常是类中的私有静态字段，它们希望将状态与线程(例如，用户ID或事务ID)关联起来。这个类提供线程局部变量。这些变量与普通变量的不同之处在于，每个访问这些变量的线程(通过其get或set方法)都有自己独立初始化的变量副本。

> 1. 内部构建了一个 `ThreadLocalMap` 对象，以当前 `ThreadLocal` 对象为 `Key`，以需要存放的值为 `Value`
> 2. `ThreadLocalMap` 被当前线程所持有，并非为 `ThreadLocal` 所持有，这便是变量线程间隔离的关键
> 3. `ThreadLocalMap` 内部为数组结构，可以存储多份数据，数组默认长度为 `16`
> 4. 内存泄漏问题，主要是由于 `ThreadLocalMap` 中的 `Key` (即 `ThreadLocal`) 是一个弱引用实现，所以在 `GC` 的时候会被回收掉，但是 `ThreadLocalMap`、`Value` 是强引用对象，此时就可能出现 `Key` 为 `null` 但是 `Value` 有值的情况。（假设把弱引用变成强引用，这样无用的对象 `key` 和 `value` 都不为 `null`，反而不利于 `GC`，只能通过 `remove()` 方法手动清理，或者等待线程结束生命周期。也就是说 `ThreadLocalMap` 的生命周期由持有它的线程来决定，线程如果不进入 `terminated` 状态，`ThreadLocalMap` 就不会被 `GC` 回收，这才是 `ThreadLocal` 内存泄漏的真正原因）
> 5. 线程池与 `ThreadLocal` 内存泄漏，线程的复用可以实现资源的复用，但很容易出现 `ThreadLocal` 资源错乱的情况（线程一直持有 `ThreadLocalMap`），所以合理的手动释放资源既能有效避免内存泄漏，也能实现物理线程与逻辑线程间的封闭隔离

### 参考链接: 

- [ThreadLocal 是什么？有哪些使用场景？](https://blog.csdn.net/meism5/article/details/90413860?utm_medium=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.baidujs&dist_request_id=&depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromMachineLearnPai2-1.baidujs)
- [ThreadLocal是什么？怎么用？为什么用它？有什么缺点？](https://zhuanlan.zhihu.com/p/192997550)


## JAX-RS 规范

### Application

指代 `Rest Application` ，即 `Rest 应用` ，可以是基于 `Servlet Container` 的，也可以是 `Standalone(独立)` 的

### Demo

```java
public class RestClientDemo {

    public static void main(String[] args) {
        Client client = ClientBuilder.newClient();
        Response response = client
                .target("http://127.0.0.1:8080/hello/world")      // WebTarget
                .request() // Invocation.Builder
                .get();                                     //  Response

        String content = response.readEntity(String.class);

        System.out.println(content);

    }
}
```

### 逻辑步骤

1. 构建 URI - 请求资源
   - UriBuilder
2. 确定请求方法 - GET、POST
   - @HttpMethod
3. 设置请求头和参数 - Headers、Parameters
   - Header Name 和 Header Value（多值）
   - Parameter Name 和 Parameter Value（多值）
   - 数据结构： Map<String,List<String>> - MultivaluedMap
4. 设置请求主体（可选） - Body 
   - 二进制流 
     - 可以转换为 Reader
5. URI -> 设置到请求
   - HTTP 客户端发送请求
        - JDK HttpURLConnection
        - Apache HttpClient 3.x
        - Apache HttpComponents
        - OkHttp
6. 执行请求（发送到 Server 服务器）
   - Servlet Stack（Tomcat、Jetty、Undertown）
   - Spring WebFlux（Netty Web Server）
   - Vert.x
7. 处理响应
    1. 正确响应（200，2XX）
        - 状态码
        - 响应头
            - 数据结构： Map<String,List<String>>
        - 响应主体
            - 二进制
    2. 异常响应 - ExceptionMapper
        1. 请求有误（4XX）
        2. 服务器问题（5XX）
        3. 请求转移（3XX）

#### Client

通过SPI技术进行 javax.ws.rs.client.ClientBuilder 其实现查找，并调用 ClientBuilder#build() 方法进行 Client 创建



## 相关技术

- 假设一个 Tomcat JVM 进程，三个 Web Apps，会不会相互冲突？（不会冲突）

- static 字段是 JVM 缓存吗？（是 ClassLoader 缓存）

- 命令行启动debug: java -Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=y -jar user-web\target\user-web-v1-SNAPSHOT.jar