package com.qritiooo.translationagency.cache;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CacheManager {

    private static final Logger log = LoggerFactory.getLogger(CacheManager.class);

    private final Map<CacheKey, Object> storage = new HashMap<>();

    @SuppressWarnings("unchecked")
    public synchronized <T> T computeIfAbsent(CacheKey key, Supplier<T> supplier) {
        if (storage.containsKey(key)) {
            log.debug("Cache hit for key: {}", key);
            return (T) storage.get(key);
        }
        log.debug("Cache miss for key: {}", key);
        T result = supplier.get();
        storage.put(key, result);
        log.debug("Cached result for key: {}", key);
        return result;
    }

    public synchronized void invalidate(Class<?>... entityClasses) {
        var classesList = Arrays.asList(entityClasses);
        log.debug("Invalidating cache for entities: {}", classesList);
        storage.keySet().removeIf(key -> classesList.contains(key.entityClass()));
    }
}
