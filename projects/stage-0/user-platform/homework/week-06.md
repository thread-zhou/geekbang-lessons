# 第六周作业

## 内容

### 提供一套抽象 API 实现对象的序列化和反序列化

### 通过 Lettuce 实现一套 Redis CacheManager 以及 Cache

## 完成情况

- [x] 提供一套抽象 API 实现对象的序列化和反序列化

> 这里参考了 `io.lettuce.core.codec.RedisCodec` 设计，将 `KeyCodec` 和 `ValueCodec` 提取出来，独立存在，因为我认为这样的方式更加的自由。
> 
> 另，数据传输的载体使用 `io.netty.buffer.ByteBuf`，但完整实现之后发现设计上还是存在问题，数据在流转时不仅存在序列化与反序列化的消耗，还会存在数据格式的相互转换，这是一个很大的提升点。
> 
> Codec API 与 Cache 的集成是通过拓展 `org.geektimes.cache.AbstractCache` 实现，实现类为: `org.geektimes.cache.AbstractCodecAbleCache`，个人认为这样的实现方式并不优雅。当然，还可以通过动态代理的方式进行实现，只不过我这里没有进行相关的实现。
> 
> Codec API 实现放在了 `user-boot` 模块中，基础接口为 `org.geektimes.boot.codec.Codec`

1. 顶层接口

```java
package org.geektimes.boot.codec;

import io.netty.buffer.ByteBuf;

/**
 * 自定义编解码接口
 */
public interface Codec {

    /**
     * 编码动作
     * @param value
     * @return
     */
    ByteBuf encode(Object value);

    /**
     * 解码动作
     * @param value
     * @param <T>
     * @return
     */
    <T> T decode(ByteBuf value);

    /**
     * 判断是否支持
     * @param clazz
     * @return
     */
    boolean support(Class clazz);
}

```

2. 基于 `JSON` 的实现

```java
package org.geektimes.boot.codec;

import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

import java.nio.charset.StandardCharsets;

/**
 * {@link Codec} Implementation By Json
 */
public class JsonBasedCodec implements Codec{

    private static final JsonBasedCodec JSON_BASED_CODEC_INSTANCE = new JsonBasedCodec();

    private JsonBasedCodec(){}

    public static final Codec getInstance(){
        return JSON_BASED_CODEC_INSTANCE;
    }

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

```
3. 基于 `Object Serializer` 的实现

```java
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

```
4. `Codec` 接口与 `Cache` 的集成

```java
package org.geektimes.cache;

import io.netty.buffer.ByteBuf;
import org.geektimes.boot.codec.Codec;
import org.geektimes.boot.codec.JsonBasedCodec;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractCodecAbleCache<K, V> extends AbstractCache<K, V>{

    private final Codec keyCodec;

    private final Codec valueCodec;

    protected AbstractCodecAbleCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration, Codec keyCodec, Codec valueCodec) {
        super(cacheManager, cacheName, configuration);
        this.keyCodec = Objects.nonNull(keyCodec) ? keyCodec : getDefaultCodec();
        this.valueCodec = Objects.nonNull(valueCodec) ? valueCodec : getDefaultCodec();
        assertCodec();
    }

    @Override
    protected boolean containsEntry(K key) throws CacheException, ClassCastException {
        return containsKey(keyCodec.encode(key));
    }

    protected abstract boolean containsKey(ByteBuf key);

    @Override
    protected ExpireAbleEntry<K, V> getEntry(K key) throws CacheException, ClassCastException {
        return getEntry(keyCodec.encode(key));
    }

    protected abstract ExpireAbleEntry<K,V> getEntry(ByteBuf key);

    @Override
    protected void putEntry(ExpireAbleEntry<K, V> entry) throws CacheException, ClassCastException {
        putEntry(keyCodec.encode(entry.getKey()), valueCodec.encode(entry.getValue()));
    }

    protected abstract void putEntry(ByteBuf key, ByteBuf value);

    @Override
    protected ExpireAbleEntry<K, V> removeEntry(K key) throws CacheException, ClassCastException {
        return removeEntry(keyCodec.encode(key));
    }

    protected abstract ExpireAbleEntry<K,V> removeEntry(ByteBuf key);

    @Override
    protected void clearEntries() throws CacheException {
        // TODO
    }


    @Override
    protected Set<K> keySet() {
        // TODO
        return null;
    }

    public Codec getKeyCodec() {
        return keyCodec;
    }

    public Codec getValueCodec() {
        return valueCodec;
    }

    private void assertCodec(){
        if (!keyCodec.support(configuration.getKeyType())){
            throw new CacheException("KeyCodec [" + keyCodec.getClass().getSimpleName() + "] not support Cache Key Type [" + configuration.getKeyType().getSimpleName() + "]");
        }
        if (!valueCodec.support(configuration.getValueType())){
            throw new CacheException("ValueCodec [" + valueCodec.getClass().getSimpleName() + "] not support Cache Value Type [" + configuration.getValueType().getSimpleName() + "]");
        }
    }

    private Codec getDefaultCodec(){
        return JsonBasedCodec.getInstance();
    }
}

```

--- 

- [x] 通过 Lettuce 实现一套 Redis CacheManager 以及 Cache

> 主要是参照已有的实现进行编写: 
> - JedisCache: `org.geektimes.cache.redis.jedis.JedisCache`
> - JedisCacheManager: `org.geektimes.cache.redis.jedis.JedisCacheManager`
> 
> 通过 `org.geektimes.cache.redis.lettuce.LettuceCodecAdapter` 完成 `io.lettuce.core.codec.RedisCodec` 与 `org.geektimes.boot.codec.Codec` 的适配
> 
> 参考 [Lettuce Docs](https://lettuce.io/docs/) 完成

1. `org.geektimes.cache.redis.lettuce.LettuceCodecAdapter` 实现

```java
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
        ByteBuf byteBuf = this.valueCodec.encode(v);
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

```

2. `org.geektimes.cache.redis.lettuce.LettuceCache` 实现

```java
package org.geektimes.cache.redis.lettuce;

import io.lettuce.core.KeyScanCursor;
import io.lettuce.core.api.sync.RedisCommands;
import io.netty.buffer.ByteBuf;
import org.geektimes.boot.codec.Codec;
import org.geektimes.cache.AbstractCodecAbleCache;
import org.geektimes.cache.ExpireAbleEntry;

import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class LettuceCache<K extends Serializable, V extends Serializable> extends AbstractCodecAbleCache<K, V> {

    private RedisCommands<K, V> lettuce;

    public LettuceCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration,
                        RedisCommands<K, V> lettuce, Codec keyCodec, Codec valueCodec) {
        super(cacheManager, cacheName, configuration, keyCodec, valueCodec);
        this.lettuce = lettuce;
    }

    @Override
    protected boolean containsKey(ByteBuf key) {
        K k = getKeyCodec().decode(key);
        return lettuce.exists(k) > 0;
    }

    @Override
    protected ExpireAbleEntry<K, V> getEntry(ByteBuf key) {
        K k = getKeyCodec().decode(key);
        V v = lettuce.get(k);
        return ExpireAbleEntry.of(k, v);
    }

    @Override
    protected void putEntry(ByteBuf key, ByteBuf value) {
        lettuce.set(getKeyCodec().decode(key), getValueCodec().decode(value));
    }

    @Override
    protected ExpireAbleEntry<K, V> removeEntry(ByteBuf key) {
        K k = getKeyCodec().decode(key);
        V v = lettuce.get(k);
        lettuce.del(k);
        return ExpireAbleEntry.of(k, v);
    }

    @Override
    protected Set<K> keySet() {
        KeyScanCursor<K> keyScanCursor = lettuce.scan();
        return Collections.unmodifiableSet(new HashSet<>(keyScanCursor.getKeys()));
    }

    @Override
    protected void clearEntries() throws CacheException {
        lettuce.reset();
    }
}

```

3. `org.geektimes.cache.redis.lettuce.LettuceCacheManager` 实现
```java
package org.geektimes.cache.redis.lettuce;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.geektimes.cache.AbstractCacheManager;

import javax.cache.Cache;
import javax.cache.configuration.Configuration;
import javax.cache.spi.CachingProvider;
import java.net.URI;
import java.util.Properties;

/**
 * {@link javax.cache.CacheManager} based on Lettuce
 */
public class LettuceCacheManager extends AbstractCacheManager {

    private RedisClient redisClient;
    private StatefulRedisConnection connection;

    public LettuceCacheManager(CachingProvider cachingProvider, URI uri, ClassLoader classLoader, Properties properties) {
        super(cachingProvider, uri, classLoader, properties);
        this.redisClient = RedisClient.create(uri.toString());
    }

    @Override
    protected <K, V, C extends Configuration<K, V>> Cache doCreateCache(String cacheName, C configuration) {
        LettuceCodecAdapter<K, V> lettuceCodecAdapter = new LettuceCodecAdapter<>(null, null);
        StatefulRedisConnection statefulRedisConnection = redisClient.connect(lettuceCodecAdapter);
        RedisCommands<K, V> syncCommands = statefulRedisConnection.sync();
        this.connection = statefulRedisConnection;
        return new LettuceCache(this, cacheName, configuration, syncCommands, lettuceCodecAdapter.getKeyCodec(), lettuceCodecAdapter.getValueCodec());
    }

    @Override
    protected void doClose() {
        connection.close();
        redisClient.shutdown();
    }
}

```

4. 测试代码
```java
package org.geektimes.cache;

import org.geektimes.cache.event.TestCacheEntryListener;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import java.net.URI;

import static org.geektimes.cache.configuration.ConfigurationUtils.cacheEntryListenerConfiguration;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * {@link Caching} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since
 */
public class CachingTest {

    @Test
    public void testSampleInMemory() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager(URI.create("in-memory://localhost/"), null);
        // configure the cache
        MutableConfiguration<String, Integer> config =
                new MutableConfiguration<String, Integer>()
                        .setTypes(String.class, Integer.class);

        // create the cache
        Cache<String, Integer> cache = cacheManager.createCache("simpleCache", config);

        // add listener
        cache.registerCacheEntryListener(cacheEntryListenerConfiguration(new TestCacheEntryListener<>()));

        // cache operations
        String key = "key";
        Integer value1 = 1;
        cache.put("key", value1);

        // update
        value1 = 2;
        cache.put("key", value1);

        Integer value2 = cache.get(key);
        assertEquals(value1, value2);
        cache.remove(key);
        assertNull(cache.get(key));
    }

    @Test
    public void testSampleRedis() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager(URI.create("redis://127.0.0.1:6379/"), null);
        // configure the cache
        MutableConfiguration<String, Integer> config =
                new MutableConfiguration<String, Integer>()
                        .setTypes(String.class, Integer.class);

        // create the cache
        Cache<String, Integer> cache = cacheManager.createCache("redisCache", config);

        // add listener
        cache.registerCacheEntryListener(cacheEntryListenerConfiguration(new TestCacheEntryListener<>()));

        // cache operations
        String key = "redis-key";
        Integer value1 = 1;
        cache.put(key, value1);

        // update
        value1 = 2;
        cache.put(key, value1);

        Integer value2 = cache.get(key);
        assertEquals(value1, value2);
        cache.remove(key);
        assertNull(cache.get(key));
    }

    @Test
    public void testLettuce() {
        CachingProvider cachingProvider = Caching.getCachingProvider();
        CacheManager cacheManager = cachingProvider.getCacheManager(URI.create("redis://localhost:6379/0"), null);
        // configure the cache
        MutableConfiguration<String, Integer> config =
                new MutableConfiguration<String, Integer>()
                        .setTypes(String.class, Integer.class);

        // create the cache
        Cache<String, Integer> cache = cacheManager.createCache("lettuce-redisCache", config);

        // add listener
        cache.registerCacheEntryListener(cacheEntryListenerConfiguration(new TestCacheEntryListener<>()));

        // cache operations
        String key = "redis-lettuce";
        Integer value1 = 5;
        cache.put(key, value1);

        // update
        value1 = 6;
        cache.put(key, value1);

        Integer value2 = cache.get(key);
        assertEquals(value1, value2);
        cache.remove(key);
        assertNull(cache.get(key));
    }
    
}

```
