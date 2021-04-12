package org.geektimes.cache.event;

import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.event.CacheEntryEvent;
import java.util.LinkedList;
import java.util.List;

/**
 * The Publisher of {@link javax.cache.event.CacheEntryEvent}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0
 */
public class CacheEntryEventPublisher {

    private List<ConditionalCacheEntryEventListener> listeners = new LinkedList<>();

    public void registerCacheEntryListener(CacheEntryListenerConfiguration configuration) {
        CacheEntryEventListenerAdapter listenerAdapter = new CacheEntryEventListenerAdapter(configuration);
        listeners.add(listenerAdapter);
    }

    public void deregisterCacheEntryListener(CacheEntryListenerConfiguration configuration) {
        CacheEntryEventListenerAdapter listenerAdapter = new CacheEntryEventListenerAdapter(configuration);
        listeners.remove(listenerAdapter);
    }

    public <K, V> void publish(CacheEntryEvent<? extends K, ? extends V> event) {
        listeners.forEach(listener -> listener.onEvent(event));
    }


}
