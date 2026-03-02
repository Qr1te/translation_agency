package com.qritiooo.translationagency.cache;

import java.util.function.Supplier;

public interface CacheStore {

    <T> T getOrLoad(CacheKey key, Supplier<T> loader);

    void clear();
}
