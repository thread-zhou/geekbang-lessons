package org.geektimes.rest.demo;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * @ClassName: HttpURLConnectionDemo
 * @Description: TODO
 * @author: zhoujian
 * @date: 2021/3/29 20:03
 * @version: 1.0
 */
public class HttpURLConnectionDemo {

    public static void main(String[] args) throws Throwable {
        URI uri = new URI("http://127.0.0.1:9090/user/sign");
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        try (InputStream inputStream = connection.getInputStream()) {
            System.out.println(IOUtils.toString(inputStream, "UTF-8"));
        }
        // 关闭连接
        connection.disconnect();
    }
}
