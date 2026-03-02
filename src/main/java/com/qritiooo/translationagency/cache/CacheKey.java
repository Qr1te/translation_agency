package com.qritiooo.translationagency.cache;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class CacheKey {

    private final String namespace;
    private final String operation;
    private final List<Object> parameters;

    private CacheKey(String namespace, String operation, List<Object> parameters) {
        this.namespace = namespace;
        this.operation = operation;
        this.parameters = parameters;
    }

    public static CacheKey of(String namespace, String operation, Object... parameters) {
        return new CacheKey(
                namespace,
                operation,
                new ArrayList<>(Arrays.asList(parameters))
        );
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof CacheKey cacheKey)) {
            return false;
        }
        return Objects.equals(namespace, cacheKey.namespace)
                && Objects.equals(operation, cacheKey.operation)
                && Objects.equals(parameters, cacheKey.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, operation, parameters);
    }
}
