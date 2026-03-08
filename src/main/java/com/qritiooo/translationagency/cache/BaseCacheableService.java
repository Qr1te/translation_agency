package com.qritiooo.translationagency.cache;

public abstract class BaseCacheableService implements CacheableService {

    private final CacheStore cacheStore = new HashMapCacheStore();

    @Override
    public final CacheStore getCacheStore() {
        return cacheStore;
    }

    protected void runAndInvalidate(Runnable action) {
        action.run();
        invalidateCache();
    }
}
