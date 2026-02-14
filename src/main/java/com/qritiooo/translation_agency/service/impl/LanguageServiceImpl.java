package com.qritiooo.translation_agency.service.impl;

import com.qritiooo.translation_agency.dto.LanguageDto;
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
    public LanguageDto create(LanguageDto dto) {
        Language l = new Language();
        LanguageMapper.updateEntity(l, dto);
        return LanguageMapper.toDto(repo.save(l));
    }

    @Override
    public LanguageDto update(Integer id, LanguageDto dto) {
        Language l = repo.findById(id).orElseThrow();
        LanguageMapper.updateEntity(l, dto);
        return LanguageMapper.toDto(repo.save(l));
    }

    @Override
    public LanguageDto getById(Integer id) {
        return LanguageMapper.toDto(repo.findById(id).orElseThrow());
    }

    @Override
    public List<LanguageDto> getAll() {
        return repo.findAll().stream().map(LanguageMapper::toDto).toList();
    }

    @Override
    public void delete(Integer id) {
        repo.deleteById(id);
    }
}