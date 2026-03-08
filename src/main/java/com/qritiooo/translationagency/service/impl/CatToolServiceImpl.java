package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.dto.request.CatToolRequest;
import com.qritiooo.translationagency.dto.response.CatToolResponse;
import com.qritiooo.translationagency.exception.NotFoundException;
import com.qritiooo.translationagency.mapper.CatToolMapper;
import com.qritiooo.translationagency.model.CatTool;
import com.qritiooo.translationagency.repository.CatToolRepository;
import com.qritiooo.translationagency.service.CatToolService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CatToolServiceImpl extends BaseCacheableService implements CatToolService {

    private final CatToolRepository repo;

    @Override
    public CatToolResponse create(CatToolRequest request) {
        CatTool tool = new CatTool();
        CatToolMapper.updateEntity(tool, request);
        return saveAndMap(tool);
    }

    @Override
    public CatToolResponse update(Integer id, CatToolRequest request) {
        CatTool tool = getCatToolOrThrow(id);
        CatToolMapper.updateEntity(tool, request);
        return saveAndMap(tool);
    }

    @Override
    public CatToolResponse patch(Integer id, CatToolRequest request) {
        CatTool tool = getCatToolOrThrow(id);
        CatToolMapper.patchEntity(tool, request);
        return saveAndMap(tool);
    }

    @Override
    public CatToolResponse getById(Integer id) {
        return CatToolMapper.toResponse(getCatToolOrThrow(id));
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
        runAndInvalidate(() -> repo.deleteById(id));
    }

    @Override
    public String getCacheNamespace() {
        return "catTool";
    }

    private CatTool getCatToolOrThrow(Integer id) {
        return repo.findById(id).orElseThrow(
                () -> new NotFoundException("CAT tool not found with id: " + id)
        );
    }

    private CatToolResponse saveAndMap(CatTool tool) {
        CatToolResponse response = CatToolMapper.toResponse(repo.save(tool));
        invalidateCache();
        return response;
    }
}
