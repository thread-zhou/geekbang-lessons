package org.geektimes.web.mvc.render;

import com.alibaba.fastjson.JSON;
import org.geektimes.web.mvc.handler.RequestHandlerChain;

import java.io.PrintWriter;

/**
 * @ClassName: JsonRender
 * @Description: 这个Render返回json数据，当Handler请求发现返回数据为json格式时，就用这个Render
 * @author: zhoujian
 * @date: 2021/3/5 20:57
 * @version: 1.0
 */
public class JsonRender implements Render{

    private Object jsonData;
    public JsonRender(Object jsonData) {
        this.jsonData = jsonData;
    }

    @Override
    public void render(RequestHandlerChain handlerChain) throws Exception {
        // 设置响应头
        handlerChain.getResponse().setContentType("application/json");
        handlerChain.getResponse().setCharacterEncoding("UTF-8");
        // 向响应中写入数据
        try (PrintWriter writer = handlerChain.getResponse().getWriter()) {
            writer.write(JSON.toJSONString(jsonData));
            writer.flush();
        }
    }
}
