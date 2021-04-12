package org.geektimes.cache.configuration;


import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.CompleteConfiguration;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableCacheEntryListenerConfiguration;
import javax.cache.event.CacheEntryEventFilter;
import javax.cache.event.CacheEntryListener;

/**
 * Cache {@link Configuration} utilities class
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0
 */
public abstract class ConfigurationUtils {

    /**
     * As an immutable instance of {@link CompleteConfiguration}
     *
     * @param configuration {@link Configuration}
     * @param <K>           the type of key
     * @param <V>           the type of value
     * @return non-null
     * @see ImmutableCompleteConfiguration
     */
    public static <K, V> CompleteConfiguration<K, V> completeConfiguration(Configuration<K, V> configuration) {
        return new ImmutableCompleteConfiguration(configuration);
    }

    public static <K, V> CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration(CacheEntryListener<? super K, ? super V> listener) {
        return cacheEntryListenerConfiguration(listener, null);
    }

    public static <K, V> CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration(CacheEntryListener<? super K, ? super V> listener,
                                                                                               CacheEntryEventFilter<? super K, ? super V> filter) {
        return cacheEntryListenerConfiguration(listener, filter, true);
    }

    public static <K, V> CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration(CacheEntryListener<? super K, ? super V> listener,
                                                                                               CacheEntryEventFilter<? super K, ? super V> filter,
                                                                                               boolean isOldValueRequired) {
        return cacheEntryListenerConfiguration(listener, filter, isOldValueRequired, true);
    }

    public static <K, V> CacheEntryListenerConfiguration<K, V> cacheEntryListenerConfiguration(CacheEntryListener<? super K, ? super V> listener,
                                                                                               CacheEntryEventFilter<? super K, ? super V> filter,
                                                                                               boolean isOldValueRequired,
                                                                                               boolean isSynchronous) {
        return new MutableCacheEntryListenerConfiguration<>(() -> listener, () -> filter, isOldValueRequired, isSynchronous);
    }
}
