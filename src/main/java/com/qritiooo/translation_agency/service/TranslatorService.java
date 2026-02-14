package com.qritiooo.translation_agency.service;

import com.qritiooo.translation_agency.dto.TranslatorDto;

import java.util.List;

public interface TranslatorService {
    TranslatorDto create(TranslatorDto dto);
    TranslatorDto update(Integer id, TranslatorDto dto);
    TranslatorDto getById(Integer id);
    List<TranslatorDto> getAll();
    void delete(Integer id);
}
