package org.geektimes.boot.codec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.io.*;

/**
 * {@link Codec} Implementation By Object Serializer
 */
public class ObjectBasedCodec implements Codec{

    private static final ObjectBasedCodec OBJECT_BASED_CODEC_INSTANCE = new ObjectBasedCodec();

    private ObjectBasedCodec(){}

    public static final Codec getInstance(){
        return OBJECT_BASED_CODEC_INSTANCE;
    }

    @Override
    public ByteBuf encode(Object value) {
        byte[] bytes = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream)
        ) {
            // Key -> byte[]
            objectOutputStream.writeObject(value);
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            throw new CodecException(e);
        }
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeBytes(bytes);
        return byteBuf;
    }

    @Override
    public <T> T decode(ByteBuf value) {
        if (value == null) {
            return null;
        }
        T target = null;
        byte[] bytes = ByteBufUtil.getBytes(value);
        if (bytes != null && bytes.length > 0) {
            try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                 ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)
            ) {
                // byte[] -> Value
                target = (T) objectInputStream.readObject();
            } catch (Exception e) {
                throw new CodecException(e);
            }
        }
        return target;
    }

    @Override
    public boolean support(Class clazz) {
        return Serializable.class.isAssignableFrom(clazz);
    }
}
