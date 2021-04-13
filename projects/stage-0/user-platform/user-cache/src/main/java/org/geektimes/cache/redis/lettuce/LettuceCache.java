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
        return lettuce.exists(getKeyCodec().decode(key)) > 0;
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
