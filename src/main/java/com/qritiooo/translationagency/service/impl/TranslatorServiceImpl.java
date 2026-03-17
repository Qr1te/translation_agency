package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.config.CacheNames;
import com.qritiooo.translationagency.dto.request.TranslatorLanguageRequest;
import com.qritiooo.translationagency.dto.request.TranslatorRequest;
import com.qritiooo.translationagency.dto.response.TranslatorResponse;
import com.qritiooo.translationagency.exception.BadRequestException;
import com.qritiooo.translationagency.exception.NotFoundException;
import com.qritiooo.translationagency.mapper.TranslatorMapper;
import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.model.Translator;
import com.qritiooo.translationagency.model.TranslatorLanguage;
import com.qritiooo.translationagency.repository.LanguageRepository;
import com.qritiooo.translationagency.repository.OrderRepository;
import com.qritiooo.translationagency.repository.TranslatorRepository;
import com.qritiooo.translationagency.service.TranslatorService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = CacheNames.TRANSLATORS_ALL)
public class TranslatorServiceImpl implements TranslatorService {

    private final TranslatorRepository translatorRepo;
    private final LanguageRepository languageRepo;
    private final OrderRepository orderRepo;

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public TranslatorResponse create(TranslatorRequest request) {
        Translator translator = new Translator();
        TranslatorMapper.updateEntity(translator, request);
        syncLanguages(translator, request.getLanguages());
        return TranslatorMapper.toResponse(translatorRepo.save(translator));
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public TranslatorResponse update(Integer id, TranslatorRequest request) {
        Translator translator = getTranslatorOrThrow(id);
        TranslatorMapper.updateEntity(translator, request);
        syncLanguages(translator, request.getLanguages());
        return TranslatorMapper.toResponse(translatorRepo.save(translator));
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public TranslatorResponse patch(Integer id, TranslatorRequest request) {
        Translator translator = getTranslatorOrThrow(id);
        TranslatorMapper.patchEntity(translator, request);
        if (request.getLanguages() != null) {
            syncLanguages(translator, request.getLanguages());
        }
        return TranslatorMapper.toResponse(translatorRepo.save(translator));
    }

    @Override
    public TranslatorResponse getById(Integer id) {
        return TranslatorMapper.toResponse(getTranslatorOrThrow(id));
    }

    @Override
    @Cacheable(sync = true)
    public List<TranslatorResponse> getAll() {
        return translatorRepo.findAll().stream().map(TranslatorMapper::toResponse).toList();
    }

    @Override
    @Transactional
    @CacheEvict(allEntries = true)
    public void delete(Integer id) {
        Translator translator = getTranslatorOrThrow(id);
        orderRepo.findByTranslator_Id(id).forEach(order -> order.setTranslator(null));
        translatorRepo.delete(translator);
    }

    private Translator getTranslatorOrThrow(Integer id) {
        return translatorRepo.findById(id).orElseThrow(
                () -> new NotFoundException("Translator not found with id: " + id)
        );
    }

    private void syncLanguages(
            Translator translator,
            List<TranslatorLanguageRequest> languageRequests
    ) {
        if (languageRequests == null) {
            translator.getTranslatorLanguages().clear();
            return;
        }

        ensureNoDuplicateLanguageIds(languageRequests);
        translator.getTranslatorLanguages().clear();

        for (TranslatorLanguageRequest languageRequest : languageRequests) {
            Integer languageId = languageRequest.getLanguageId();
            if (languageId == null) {
                throw new BadRequestException("languageId is required for each language item");
            }
            if (languageRequest.getProficiencyLevel() == null) {
                throw new BadRequestException(
                        "proficiencyLevel is required for languageId: " + languageId
                );
            }

            Language language = languageRepo.findById(languageId).orElseThrow(
                    () -> new NotFoundException("Language not found with id: " + languageId)
            );

            TranslatorLanguage translatorLanguage = new TranslatorLanguage();
            translatorLanguage.setTranslator(translator);
            translatorLanguage.setLanguage(language);
            translatorLanguage.setProficiencyLevel(languageRequest.getProficiencyLevel());
            translator.getTranslatorLanguages().add(translatorLanguage);
        }
    }

    private void ensureNoDuplicateLanguageIds(List<TranslatorLanguageRequest> languageRequests) {
        Set<Integer> seenLanguageIds = new HashSet<>();
        for (TranslatorLanguageRequest languageRequest : languageRequests) {
            Integer languageId = languageRequest.getLanguageId();
            if (languageId == null) {
                continue;
            }
            if (!seenLanguageIds.add(languageId)) {
                throw new BadRequestException("Duplicate languageId in request: " + languageId);
            }
        }
    }
}
