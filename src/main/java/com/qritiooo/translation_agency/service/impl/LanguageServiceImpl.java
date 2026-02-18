package com.qritiooo.translation_agency.service.impl;

import com.qritiooo.translation_agency.dto.request.LanguageRequest;
import com.qritiooo.translation_agency.dto.response.LanguageResponse;
import com.qritiooo.translation_agency.mapper.LanguageMapper;
import com.qritiooo.translation_agency.model.Language;
import com.qritiooo.translation_agency.repository.LanguageRepository;
import com.qritiooo.translation_agency.service.LanguageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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
