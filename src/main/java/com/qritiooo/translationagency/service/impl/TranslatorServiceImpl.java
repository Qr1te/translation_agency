package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.dto.request.TranslatorRequest;
import com.qritiooo.translationagency.dto.response.TranslatorResponse;
import com.qritiooo.translationagency.mapper.TranslatorMapper;
import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.model.Translator;
import com.qritiooo.translationagency.repository.LanguageRepository;
import com.qritiooo.translationagency.repository.TranslatorRepository;
import com.qritiooo.translationagency.service.TranslatorService;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

