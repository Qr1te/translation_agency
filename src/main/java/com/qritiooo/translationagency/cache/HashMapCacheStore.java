package com.qritiooo.translationagency.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public class HashMapCacheStore implements CacheStore {

    private final Map<CacheKey, Object> cache = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getOrLoad(CacheKey key, Supplier<T> loader) {
        return (T) cache.computeIfAbsent(key, unused -> loader.get());
    }

    @Override
    public void clear() {
        cache.clear();
    }
}
