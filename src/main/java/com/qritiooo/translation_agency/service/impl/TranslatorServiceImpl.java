package com.qritiooo.translation_agency.service.impl;

import com.qritiooo.translation_agency.dto.TranslatorDto;
import com.qritiooo.translation_agency.mapper.TranslatorMapper;
import com.qritiooo.translation_agency.model.Language;
import com.qritiooo.translation_agency.model.Translator;
import com.qritiooo.translation_agency.repository.LanguageRepository;
import com.qritiooo.translation_agency.repository.TranslatorRepository;
import com.qritiooo.translation_agency.service.TranslatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TranslatorServiceImpl implements TranslatorService {

    private final TranslatorRepository translatorRepo;
    private final LanguageRepository languageRepo;

    @Override
    public TranslatorDto create(TranslatorDto dto) {
        Translator t = new Translator();
        TranslatorMapper.updateEntity(t, dto);

        if (dto.getLanguageIds() != null) {
            List<Language> langs = languageRepo.findAllById(dto.getLanguageIds());
            t.setLanguages(new HashSet<>(langs));
        }

        return TranslatorMapper.toDto(translatorRepo.save(t));
    }

    @Override
    public TranslatorDto update(Integer id, TranslatorDto dto) {
        Translator t = translatorRepo.findById(id).orElseThrow();
        TranslatorMapper.updateEntity(t, dto);

        if (dto.getLanguageIds() != null) {
            List<Language> langs = languageRepo.findAllById(dto.getLanguageIds());
            t.setLanguages(new HashSet<>(langs));
        }

        return TranslatorMapper.toDto(translatorRepo.save(t));
    }

    @Override
    public TranslatorDto getById(Integer id) {
        return TranslatorMapper.toDto(translatorRepo.findById(id).orElseThrow());
    }

    @Override
    public List<TranslatorDto> getAll() {
        return translatorRepo.findAll().stream().map(TranslatorMapper::toDto).toList();
    }

    @Override
    public void delete(Integer id) {
        translatorRepo.deleteById(id);
    }
}
