package com.qritiooo.translation_agency.service.impl;

import com.qritiooo.translation_agency.dto.request.TranslatorRequest;
import com.qritiooo.translation_agency.dto.response.TranslatorResponse;
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
    public TranslatorResponse create(TranslatorRequest request) {
        Translator t = new Translator();
        TranslatorMapper.updateEntity(t, request);

        if (request.getLanguageIds() != null) {
            List<Language> langs = languageRepo.findAllById(request.getLanguageIds());
            t.setLanguages(new HashSet<>(langs));
        }

        return TranslatorMapper.toResponse(translatorRepo.save(t));
    }

    @Override
    public TranslatorResponse update(Integer id, TranslatorRequest request) {
        Translator t = translatorRepo.findById(id).orElseThrow();
        TranslatorMapper.updateEntity(t, request);

        if (request.getLanguageIds() != null) {
            List<Language> langs = languageRepo.findAllById(request.getLanguageIds());
            t.setLanguages(new HashSet<>(langs));
        }

        return TranslatorMapper.toResponse(translatorRepo.save(t));
    }

    @Override
    public TranslatorResponse getById(Integer id) {
        return TranslatorMapper.toResponse(translatorRepo.findById(id).orElseThrow());
    }

    @Override
    public List<TranslatorResponse> getAll() {
        return translatorRepo.findAll().stream().map(TranslatorMapper::toResponse).toList();
    }

    @Override
    public void delete(Integer id) {
        translatorRepo.deleteById(id);
    }
}
