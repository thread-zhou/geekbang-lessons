package org.geektimes.cache;

import javax.cache.Cache;
import java.io.Serializable;
import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Expirable {@link Cache.Entry}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see Map.Entry
 * @see Cache.Entry
 * @since 1.0
 */
public class ExpireAbleEntry<K, V> implements Cache.Entry<K, V>, Serializable {

    private final K key;

    private V value;

    private long timestamp;

    private ExpireAbleEntry(K key, V value) throws NullPointerException {
        requireKeyNotNull(key);
        this.key = key;
        this.setValue(value);
        this.timestamp = Long.MAX_VALUE; // default
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setValue(V value) {
        requireValueNotNull(value);
        this.value = value;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() >= timestamp;
    }

    @Override
    public <T> T unwrap(Class<T> clazz) {
        T value = null;
        try {
            value = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    @Override
    public String toString() {
        return "ExpirableEntry{" +
                "key=" + key +
                ", value=" + value +
                ", timestamp=" + timestamp +
                '}';
    }

    public static <K, V> ExpireAbleEntry<K, V> of(Map.Entry<K, V> entry) {
        return new ExpireAbleEntry(entry.getKey(), entry.getValue());
    }

    public static <K, V> ExpireAbleEntry<K, V> of(K key, V value) {
        return new ExpireAbleEntry(key, value);
    }

    public static <K> void requireKeyNotNull(K key) {
        requireNonNull(key, "The key must not be null.");
    }

    public static <V> void requireValueNotNull(V value) {
        requireNonNull(value, "The value must not be null.");
    }

    public static <V> void requireOldValueNotNull(V oldValue) {
        requireNonNull(oldValue, "The oldValue must not be null.");
    }
}
