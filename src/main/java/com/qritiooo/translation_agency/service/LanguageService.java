package com.qritiooo.translation_agency.service;

import com.qritiooo.translation_agency.dto.LanguageDto;

import java.util.List;

public interface LanguageService {
    LanguageDto create(LanguageDto dto);
    LanguageDto update(Integer id, LanguageDto dto);
    LanguageDto getById(Integer id);
    List<LanguageDto> getAll();
    void delete(Integer id);
}
