package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.cache.CacheStore;
import com.qritiooo.translationagency.cache.CacheableService;
import com.qritiooo.translationagency.cache.HashMapCacheStore;
import com.qritiooo.translationagency.dto.request.CatToolRequest;
import com.qritiooo.translationagency.dto.response.CatToolResponse;
import com.qritiooo.translationagency.mapper.CatToolMapper;
import com.qritiooo.translationagency.model.CatTool;
import com.qritiooo.translationagency.repository.CatToolRepository;
import com.qritiooo.translationagency.service.CatToolService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatToolServiceImpl implements CatToolService, CacheableService {

    private final CatToolRepository repo;
    private final CacheStore cacheStore = new HashMapCacheStore();

    @Override
    public CatToolResponse create(CatToolRequest request) {
        CatTool tool = new CatTool();
        CatToolMapper.updateEntity(tool, request);
        CatToolResponse response = CatToolMapper.toResponse(repo.save(tool));
        invalidateCache();
        return response;
    }

    @Override
    public CatToolResponse update(Integer id, CatToolRequest request) {
        CatTool tool = repo.findById(id).orElseThrow();
        CatToolMapper.updateEntity(tool, request);
        CatToolResponse response = CatToolMapper.toResponse(repo.save(tool));
        invalidateCache();
        return response;
    }

    @Override
    public CatToolResponse patch(Integer id, CatToolRequest request) {
        CatTool tool = repo.findById(id).orElseThrow();
        CatToolMapper.patchEntity(tool, request);
        CatToolResponse response = CatToolMapper.toResponse(repo.save(tool));
        invalidateCache();
        return response;
    }

    @Override
    public CatToolResponse getById(Integer id) {
        return CatToolMapper.toResponse(repo.findById(id).orElseThrow());
    }

    @Override
    public List<CatToolResponse> getAll() {
        return getOrLoad(
                "getAll",
                () -> repo.findAll().stream().map(CatToolMapper::toResponse).toList()
        );
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
        invalidateCache();
    }

    @Override
    public String getCacheNamespace() {
        return "catTool";
    }

    @Override
    public CacheStore getCacheStore() {
        return cacheStore;
    }
}
