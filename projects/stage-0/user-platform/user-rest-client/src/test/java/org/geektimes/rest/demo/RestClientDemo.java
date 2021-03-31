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
