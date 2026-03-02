package com.qritiooo.translationagency.cache;

import java.util.function.Supplier;

public interface CacheableService {

    String getCacheNamespace();

    CacheStore getCacheStore();

    default <T> T getOrLoad(String operation, Supplier<T> loader, Object... parameters) {
        return getCacheStore().getOrLoad(
                CacheKey.of(getCacheNamespace(), operation, parameters),
                loader
        );
    }

    default void invalidateCache() {
        getCacheStore().clear();
    }
}
