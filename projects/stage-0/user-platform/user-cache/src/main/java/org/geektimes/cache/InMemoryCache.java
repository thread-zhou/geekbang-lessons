package org.geektimes.cache;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheManager;
import javax.cache.configuration.Configuration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * In Memory no-thread-safe {@link Cache}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0
 */
public class InMemoryCache<K, V> extends AbstractCache<K, V> {

    private final Map<K, ExpireAbleEntry<K, V>> cache;

    public InMemoryCache(CacheManager cacheManager, String cacheName, Configuration<K, V> configuration) {
        super(cacheManager, cacheName, configuration);
        this.cache = new HashMap<>();
    }

    @Override
    protected boolean containsEntry(K key) throws CacheException, ClassCastException {
        return cache.containsKey(key);
    }

    @Override
    protected ExpireAbleEntry<K, V> getEntry(K key) throws CacheException, ClassCastException {
        return cache.get(key);
    }

    @Override
    protected void putEntry(ExpireAbleEntry<K, V> newEntry) throws CacheException, ClassCastException {
        K key = newEntry.getKey();
        cache.put(key, newEntry);
    }

    @Override
    protected ExpireAbleEntry<K, V> removeEntry(K key) throws CacheException, ClassCastException {
        return cache.remove(key);
    }

    @Override
    protected void doClear() throws CacheException {
        cache.clear();
    }

    @Override
    protected Iterator<Entry<K, V>> newIterator() {
        return (Iterator) cache.values().iterator();
    }

}