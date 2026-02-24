package com.qritiooo.translationagency.service.impl;

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
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository repo;

    @Override
    public LanguageResponse create(LanguageRequest request) {
        Language l = new Language();
        LanguageMapper.updateEntity(l, request);
        return LanguageMapper.toResponse(repo.save(l));
    }

    @Override
    public LanguageResponse update(Integer id, LanguageRequest request) {
        Language l = repo.findById(id).orElseThrow();
        LanguageMapper.updateEntity(l, request);
        return LanguageMapper.toResponse(repo.save(l));
    }

    @Override
    public LanguageResponse getById(Integer id) {
        return LanguageMapper.toResponse(repo.findById(id).orElseThrow());
    }

    @Override
    public List<LanguageResponse> getAll() {
        return repo.findAll().stream().map(LanguageMapper::toResponse).toList();
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }
}

