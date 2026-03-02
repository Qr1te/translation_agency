package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.cache.CacheStore;
import com.qritiooo.translationagency.cache.CacheableService;
import com.qritiooo.translationagency.cache.HashMapCacheStore;
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
public class TranslatorServiceImpl implements TranslatorService, CacheableService {

    private final TranslatorRepository translatorRepo;
    private final LanguageRepository languageRepo;
    private final CacheStore cacheStore = new HashMapCacheStore();

    @Override
    public TranslatorResponse create(TranslatorRequest request) {
        Translator t = new Translator();
        TranslatorMapper.updateEntity(t, request);

        if (request.getLanguageIds() != null) {
            List<Language> langs = languageRepo.findAllById(request.getLanguageIds());
            t.setLanguages(new HashSet<>(langs));
        }

        TranslatorResponse response = TranslatorMapper.toResponse(translatorRepo.save(t));
        invalidateCache();
        return response;
    }

    @Override
    public TranslatorResponse update(Integer id, TranslatorRequest request) {
        Translator t = translatorRepo.findById(id).orElseThrow();
        TranslatorMapper.updateEntity(t, request);

        if (request.getLanguageIds() != null) {
            List<Language> langs = languageRepo.findAllById(request.getLanguageIds());
            t.setLanguages(new HashSet<>(langs));
        }

        TranslatorResponse response = TranslatorMapper.toResponse(translatorRepo.save(t));
        invalidateCache();
        return response;
    }

    @Override
    public TranslatorResponse patch(Integer id, TranslatorRequest request) {
        Translator t = translatorRepo.findById(id).orElseThrow();
        TranslatorMapper.patchEntity(t, request);

        if (request.getLanguageIds() != null) {
            List<Language> langs = languageRepo.findAllById(request.getLanguageIds());
            t.setLanguages(new HashSet<>(langs));
        }

        TranslatorResponse response = TranslatorMapper.toResponse(translatorRepo.save(t));
        invalidateCache();
        return response;
    }

    @Override
    public TranslatorResponse getById(Integer id) {
        return TranslatorMapper.toResponse(translatorRepo.findById(id).orElseThrow());
    }

    @Override
    public List<TranslatorResponse> getAll() {
        return getOrLoad(
                "getAll",
                () -> translatorRepo.findAll().stream().map(TranslatorMapper::toResponse).toList()
        );
    }

    @Override
    public void delete(Integer id) {
        translatorRepo.deleteById(id);
        invalidateCache();
    }

    @Override
    public String getCacheNamespace() {
        return "translator";
    }

    @Override
    public CacheStore getCacheStore() {
        return cacheStore;
    }
}

