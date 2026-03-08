package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.cache.CacheStore;
import com.qritiooo.translationagency.cache.CacheableService;
import com.qritiooo.translationagency.cache.HashMapCacheStore;
import com.qritiooo.translationagency.dto.response.LanguageResponse;
import com.qritiooo.translationagency.mapper.LanguageMapper;
import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.service.LanguageService;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService, CacheableService {

    private final CacheStore cacheStore = new HashMapCacheStore();

    @Override
    public LanguageResponse getByCode(String code) {
        return LanguageMapper.toResponse(Language.fromCode(code));
    }

    @Override
    public List<LanguageResponse> getAll() {
        return getOrLoad(
                "getAll",
                () -> Arrays.stream(Language.values()).map(LanguageMapper::toResponse).toList()
        );
    }

    @Override
    public String getCacheNamespace() {
        return "language";
    }

    @Override
    public CacheStore getCacheStore() {
        return cacheStore;
    }
}

