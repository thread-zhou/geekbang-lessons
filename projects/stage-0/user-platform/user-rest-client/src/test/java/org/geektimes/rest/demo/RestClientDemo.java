package org.geektimes.rest.demo;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;

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
}
