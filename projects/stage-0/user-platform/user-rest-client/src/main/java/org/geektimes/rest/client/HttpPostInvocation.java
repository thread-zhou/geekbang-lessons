package org.geektimes.rest.client;

import org.geektimes.rest.core.DefaultResponse;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
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
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(HttpMethod.POST);
            setRequestHeaders(connection);
            // TODO Set the cookies
            // TODO Handler Entity
            int statusCode = connection.getResponseCode();
            DefaultResponse response = new DefaultResponse();
            response.setConnection(connection);
            response.setStatus(statusCode);
            return response;

        } catch (IOException e) {
            // TODO Error handler
        }
        return null;
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

    /**
     * 将请求头带回
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
