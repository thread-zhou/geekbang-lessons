# 第五周作业

## 内容

### 修复本程序 org.geektimes.reactive.streams 包下

### 继续完善 my-rest-client POST 方法

## 完成情况

- [x] 修复本程序 org.geektimes.reactive.streams 包下

1. 通过代码阅读，发现此处并无逻辑错误，而是对于背压情况发生时如何处理消息的方式，背压是由订阅方经特定业务场景下进行触发，这里可以存在两种情况

场景：订阅方一定已经收到了该则消息

- 在当前业务场景下，订阅方需在收到消息的情境下进行是否背压的判定，若需要进行背压，应该如何处理已接受到的消息(此处直接丢弃)

```java
@Override
public void onNext(Object o) {
        // 当到达数据阈值时，取消 Publisher 给当前 Subscriber 发送数据
        if (++count > 2) {
        subscription.cancel();
        System.out.printf("收到数据：%s, 已丢弃\n", o);
        return;
        }
        System.out.println("收到数据：" + o);
        }
```
- 触发背压时，也进行消息的消费，而后

```java
@Override
public void onNext(Object o) {
        // 当到达数据阈值时，取消 Publisher 给当前 Subscriber 发送数据
        if (++count > 2) {
        subscription.cancel();
        System.out.println("收到数据：" + o);
        return;
        }
        System.out.println("收到数据：" + o);
        }
```

--- 

- [x] 继续完善 my-rest-client POST 方法

简单实现，参照 `org.geektimes.rest.client.HttpGetInvocation` 进行实现

1、 `org.geektimes.rest.client.HttpPostInvocation` 实现

```java
package org.geektimes.rest.client;

import org.geektimes.rest.core.DefaultResponse;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;

/**
 * @ClassName: HttpPostInvocation
 * @Description: {@link Invocation} Implementation for Http Post Method
 * @author: zhoujian
 * @date: 2021/3/30 22:54
 * @version: 1.0
 */
public class HttpPostInvocation implements Invocation {

    private final URI uri;

    private final URL url;

    private final MultivaluedMap<String, Object> headers;

    private final Entity<?> entity;

    HttpPostInvocation(URI uri, MultivaluedMap<String, Object> headers, Entity<?> entity) {
        this.uri = uri;
        this.headers = headers;
        try {
            this.url = uri.toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException();
        }
        this.entity = entity;
    }

    @Override
    public Invocation property(String s, Object o) {
        return null;
    }

    @Override
    public Response invoke() {
        HttpURLConnection connection = null;
        DefaultResponse response = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(HttpMethod.POST);
            setRequestHeaders(connection);
            setRequestEntity(connection);
            // TODO Set the cookies
            // TODO Handler Entity
            int statusCode = connection.getResponseCode();
            response = (DefaultResponse) Response.ok(entity.getEntity()).build();
            response.setConnection(connection);
            response.setStatus(statusCode);
            if (Objects.nonNull(entity.getEncoding())){
                response.setEncoding(entity.getEncoding());
            }

        } catch (IOException e) {
            // TODO Error handler
            response = (DefaultResponse) Response.serverError().entity(e.getMessage()).build();
        }
        return response;
    }

    @Override
    public <T> T invoke(Class<T> aClass) {
        return null;
    }

    @Override
    public <T> T invoke(GenericType<T> genericType) {
        return null;
    }

    @Override
    public Future<Response> submit() {
        return null;
    }

    @Override
    public <T> Future<T> submit(Class<T> aClass) {
        return null;
    }

    @Override
    public <T> Future<T> submit(GenericType<T> genericType) {
        return null;
    }

    @Override
    public <T> Future<T> submit(InvocationCallback<T> invocationCallback) {
        return null;
    }

    private void setRequestEntity(HttpURLConnection connection){
        if (Objects.nonNull(entity.getMediaType())){
            connection.setRequestProperty("content-type", entity.getMediaType().getType() + "/" + entity.getMediaType().getSubtype());
        }
    }

    /**
     * 设置请求头
     * @author zhoujian
     * @date 22:56 2021/3/30
     * @param connection
     * @return void
     **/
    private void setRequestHeaders(HttpURLConnection connection) {
        for (Map.Entry<String, List<Object>> entry : headers.entrySet()) {
            String headerName = entry.getKey();
            for (Object headerValue : entry.getValue()) {
                connection.setRequestProperty(headerName, headerValue.toString());
            }
        }
    }
}


```

2、测试用例编写

接口编写，对应测试方法 `testPostWithParams`

```java
package org.geektimes.projects.user.web.controller;

import org.geektimes.projects.user.domain.User;
import org.geektimes.web.mvc.controller.RestController;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

/**
 * @ClassName: RestUserController
 * @Description: TODO
 * @author: zhoujian
 * @date: 2021/3/31 13:57
 * @version: 1.0
 */
@Path("/rest")
public class RestDemoController implements RestController {

    @POST
    @Path("/123/456")
    public User testPostInvocation(HttpServletRequest request, HttpServletResponse response){
        User user = new User();
        user.setName("拂衣");
        user.setEmail("hhxhxh@163.com");
        user.setId(1l);
        return user;
    }
}

```

```java
package org.geektimes.rest.demo;

import org.geektimes.rest.domain.User;
import org.geektimes.rest.util.Maps;
import org.junit.Test;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.RuntimeDelegate;

/**
 * @ClassName: RestClientDemo
 * @Description: TODO
 * @author: zhoujian
 * @date: 2021/3/29 20:03
 * @version: 1.0
 */
public class RestClientDemo {

    public static void main(String[] args) {
        Client client = ClientBuilder.newClient();
        Response response = client
                .target("http://127.0.0.1:9090/user/sign")      // WebTarget
                .request() // Invocation.Builder
                .get();                                     //  Response

        String content = response.readEntity(String.class);

        System.out.println(content);

    }

    @Test
    public void testPost(){
        Client client = ClientBuilder.newClient();
        Response response = client.target("http://127.0.0.1:9090/user/register")
                .request()
                .post(Entity.text(String.class));
        String content = response.readEntity(String.class);

        System.out.println(content);
    }

    @Test
    public void testPostWithParams(){
        Client client = ClientBuilder.newClient();
        UriBuilder uriBuilder = RuntimeDelegate.getInstance().createUriBuilder()
                .scheme("http")
                .host("127.0.0.1")
                .port(9090)
                .path("/rest/{param1}/{param2}")
                .resolveTemplates(Maps.of("param1", "123", "param2", "456"));
        Response response = client.target(uriBuilder).request().post(Entity.json(User.class));

        User content = response.readEntity(User.class);

        System.out.println(content);
    }
}

```

