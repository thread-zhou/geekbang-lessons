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
        this.connection = redisClient.connect(lettuceCodecAdapter);
        RedisCommands<K, V> syncCommands = connection.sync();
        return new LettuceCache(this, cacheName, configuration, syncCommands, lettuceCodecAdapter.getKeyCodec(), lettuceCodecAdapter.getValueCodec());
    }

    @Override
    protected void doClose() {
        connection.close();
        redisClient.shutdown();
    }
}
