package org.geektimes.boot.codec;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

public class JsonBasedCodec implements Codec{

    @Override
    public ByteBuf encode(Object value) {
        ByteBuf byteBuf = Unpooled.buffer();
        ByteBufUtil.writeUtf8(byteBuf, getJsonStr(value));
        return byteBuf;
    }

    @Override
    public <T> T decode(ByteBuf value) {
        byte[] bytes = ByteBufUtil.getBytes(value);
        if (bytes.length > 0) {
            return jsonDecode(new String(bytes, StandardCharsets.UTF_8));
        }
        return null;
    }

    @Override
    public boolean support(Class clazz) {
        return true;
    }

    private String getJsonStr(Object value){
        return JSONObject.toJSONString(value);
    }

    private <T> T jsonDecode(String value){
        T target = (T) JSONObject.parse(String.valueOf(value));
        return target;
    }
}
