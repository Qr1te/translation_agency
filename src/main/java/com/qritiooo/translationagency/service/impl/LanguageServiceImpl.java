package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.cache.CacheStore;
import com.qritiooo.translationagency.cache.CacheableService;
import com.qritiooo.translationagency.cache.HashMapCacheStore;
import com.qritiooo.translationagency.dto.request.LanguageRequest;
import com.qritiooo.translationagency.dto.response.LanguageResponse;
import com.qritiooo.translationagency.mapper.LanguageMapper;
import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.repository.LanguageRepository;
import com.qritiooo.translationagency.service.LanguageService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService, CacheableService {

    private final LanguageRepository repo;
    private final CacheStore cacheStore = new HashMapCacheStore();

    @Override
    public LanguageResponse create(LanguageRequest request) {
        Language l = new Language();
        LanguageMapper.updateEntity(l, request);
        LanguageResponse response = LanguageMapper.toResponse(repo.save(l));
        invalidateCache();
        return response;
    }

    @Override
    public LanguageResponse update(Integer id, LanguageRequest request) {
        Language l = repo.findById(id).orElseThrow();
        LanguageMapper.updateEntity(l, request);
        LanguageResponse response = LanguageMapper.toResponse(repo.save(l));
        invalidateCache();
        return response;
    }

    @Override
    public LanguageResponse patch(Integer id, LanguageRequest request) {
        Language l = repo.findById(id).orElseThrow();
        LanguageMapper.patchEntity(l, request);
        LanguageResponse response = LanguageMapper.toResponse(repo.save(l));
        invalidateCache();
        return response;
    }

    @Override
    public LanguageResponse getById(Integer id) {
        return LanguageMapper.toResponse(repo.findById(id).orElseThrow());
    }

    @Override
    public List<LanguageResponse> getAll() {
        return getOrLoad(
                "getAll",
                () -> repo.findAll().stream().map(LanguageMapper::toResponse).toList()
        );
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
        invalidateCache();
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

