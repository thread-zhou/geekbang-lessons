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
        if (!keyCodec.support(getConfiguration().getKeyType())){
            throw new CacheException("KeyCodec [" + keyCodec.getClass().getSimpleName() + "] not support Cache Key Type [" + getConfiguration().getKeyType().getSimpleName() + "]");
        }
        if (!valueCodec.support(getConfiguration().getValueType())){
            throw new CacheException("ValueCodec [" + valueCodec.getClass().getSimpleName() + "] not support Cache Value Type [" + getConfiguration().getValueType().getSimpleName() + "]");
        }
    }

    private Codec getDefaultCodec(){
        return JsonBasedCodec.getInstance();
    }
}
