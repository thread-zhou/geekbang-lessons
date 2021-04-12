package org.geektimes.cache.redis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import org.geektimes.boot.codec.JsonBasedCodec;
import org.geektimes.boot.codec.ObjectBasedCodec;
import org.geektimes.cache.AbstractCodecAbleCache;
import org.geektimes.cache.ExpireAbleEntry;
import redis.clients.jedis.Jedis;

import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.io.*;

public class JedisCache<K extends Serializable, V extends Serializable> extends AbstractCodecAbleCache<K, V> {

    private final Jedis jedis;

    public JedisCache(CacheManager cacheManager, String cacheName,
                      Configuration<K, V> configuration, Jedis jedis) {
        super(cacheManager, cacheName, configuration, new JsonBasedCodec(), new ObjectBasedCodec());
        this.jedis = jedis;
    }

    @Override
    protected boolean containsKey(ByteBuf key) {
        return jedis.exists(ByteBufUtil.getBytes(key));
    }

    @Override
    protected ExpireAbleEntry<K, V> getEntry(ByteBuf key) {
        byte[] valueBytes = jedis.get(ByteBufUtil.getBytes(key));
        ByteBuf valueBuf = Unpooled.buffer();
        if (valueBytes != null) {
            valueBuf.writeBytes(valueBytes);
        }
        return ExpireAbleEntry.of(getKeyCodec().decode(key), getValueCodec().decode(valueBuf));
    }

    @Override
    protected void putEntry(ByteBuf key, ByteBuf value) {
        jedis.set(ByteBufUtil.getBytes(key), ByteBufUtil.getBytes(value));
    }

    @Override
    protected ExpireAbleEntry<K, V> removeEntry(ByteBuf key) {
        ExpireAbleEntry<K, V> oldEntry = getEntry(key);
        jedis.del(ByteBufUtil.getBytes(key));
        return oldEntry;
    }

    @Override
    protected void doClose() {
        this.jedis.close();
    }

}
