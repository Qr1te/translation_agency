package com.qritiooo.translationagency.cache;

import java.util.Arrays;
import java.util.List;

public record CacheKey(Class<?> entityClass, String methodName, List<Object> args) {

    public CacheKey(Class<?> entityClass, String methodName, Object... args) {
        this(entityClass, methodName, Arrays.asList(args));
    }
}
