package org.geektimes.cache.redis.lettuce;

import io.lettuce.core.codec.RedisCodec;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.geektimes.boot.codec.Codec;
import org.geektimes.boot.codec.JsonBasedCodec;
import org.geektimes.boot.codec.ObjectBasedCodec;

import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * 适配 {@link RedisCodec}, 实际序列化机制为 {@link Codec}
 * @param <K>
 * @param <V>
 */
public class LettuceCodecAdapter<K, V> implements RedisCodec<K, V> {

    private final Codec keyCodec;

    private final Codec valueCodec;

    public LettuceCodecAdapter(Codec keyCodec, Codec valueCodec){
        this.keyCodec = Objects.nonNull(keyCodec) ? keyCodec : getDefaultKeyCodec();
        this.valueCodec = Objects.nonNull(valueCodec) ? valueCodec : getDefaultValueCodec();
    }

    @Override
    public K decodeKey(ByteBuffer byteBuffer) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(byteBuffer);
        return this.keyCodec.decode(byteBuf);
    }

    @Override
    public V decodeValue(ByteBuffer byteBuffer) {
        ByteBuf byteBuf = Unpooled.wrappedBuffer(byteBuffer);
        return this.valueCodec.decode(byteBuf);
    }

    @Override
    public ByteBuffer encodeKey(K k) {
        ByteBuf byteBuf = this.keyCodec.encode(k);
        return ByteBuffer.wrap(ByteBufUtil.getBytes(byteBuf));
    }

    @Override
    public ByteBuffer encodeValue(V v) {
        ByteBuf byteBuf = this.keyCodec.encode(v);
        return ByteBuffer.wrap(ByteBufUtil.getBytes(byteBuf));
    }

    public Codec getKeyCodec() {
        return keyCodec;
    }

    public Codec getValueCodec() {
        return valueCodec;
    }

    private Codec getDefaultKeyCodec(){
        return JsonBasedCodec.getInstance();
    }

    private Codec getDefaultValueCodec(){
        return ObjectBasedCodec.getInstance();
    }
}
