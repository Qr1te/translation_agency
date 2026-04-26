package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.cache.CacheKey;
import com.qritiooo.translationagency.cache.CacheManager;
import com.qritiooo.translationagency.dto.request.TranslatorLanguageRequest;
import com.qritiooo.translationagency.dto.request.TranslatorRequest;
import com.qritiooo.translationagency.dto.response.TranslatorResponse;
import com.qritiooo.translationagency.exception.BadRequestException;
import com.qritiooo.translationagency.exception.NotFoundException;
import com.qritiooo.translationagency.mapper.TranslatorMapper;
import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.model.Order;
import com.qritiooo.translationagency.model.Translator;
import com.qritiooo.translationagency.model.TranslatorLanguage;
import com.qritiooo.translationagency.repository.LanguageRepository;
import com.qritiooo.translationagency.repository.OrderRepository;
import com.qritiooo.translationagency.repository.TranslatorRepository;
import com.qritiooo.translationagency.service.TranslatorService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TranslatorServiceImpl implements TranslatorService {

    private final TranslatorRepository translatorRepo;
    private final LanguageRepository languageRepo;
    private final OrderRepository orderRepo;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    public TranslatorResponse create(TranslatorRequest request) {
        Translator translator = new Translator();
        TranslatorMapper.updateEntity(translator, request);
        syncLanguages(translator, request.getLanguages());
        TranslatorResponse response = TranslatorMapper.toResponse(translatorRepo.save(translator));
        cacheManager.invalidate(Translator.class, Order.class);
        return response;
    }

    @Override
    @Transactional
    public TranslatorResponse update(Integer id, TranslatorRequest request) {
        Translator translator = getTranslatorOrThrow(id);
        TranslatorMapper.updateEntity(translator, request);
        syncLanguages(translator, request.getLanguages());
        TranslatorResponse response = TranslatorMapper.toResponse(translatorRepo.save(translator));
        cacheManager.invalidate(Translator.class, Order.class);
        return response;
    }

    @Override
    @Transactional
    public TranslatorResponse patch(Integer id, TranslatorRequest request) {
        Translator translator = getTranslatorOrThrow(id);
        TranslatorMapper.patchEntity(translator, request);
        if (request.getLanguages() != null) {
            syncLanguages(translator, request.getLanguages());
        }
        TranslatorResponse response = TranslatorMapper.toResponse(translatorRepo.save(translator));
        cacheManager.invalidate(Translator.class, Order.class);
        return response;
    }

    @Override
    public TranslatorResponse getById(Integer id) {
        return TranslatorMapper.toResponse(getTranslatorOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TranslatorResponse> getAll(Pageable pageable) {
        Pageable sortedPageable = withDefaultSort(pageable);
        CacheKey key = new CacheKey(
                Translator.class,
                "getAll",
                sortedPageable.getPageNumber(),
                sortedPageable.getPageSize(),
                sortedPageable.getSort().toString()
        );
        return cacheManager.computeIfAbsent(key, () -> {
            Page<Translator> translatorPage = translatorRepo.findAll(sortedPageable);
            if (translatorPage.isEmpty()) {
                return Page.empty(sortedPageable);
            }

            List<Integer> translatorIds = translatorPage.getContent().stream()
                    .map(Translator::getId)
                    .toList();
            Map<Integer, Translator> detailedTranslatorsById = translatorRepo
                    .findByIdIn(translatorIds)
                    .stream()
                    .collect(java.util.stream.Collectors.toMap(
                            Translator::getId,
                            Function.identity()
                    ));

            List<TranslatorResponse> responses = translatorPage.getContent().stream()
                    .map(translator -> detailedTranslatorsById.getOrDefault(
                            translator.getId(),
                            translator
                    ))
                    .map(TranslatorMapper::toResponse)
                    .toList();

            return new PageImpl<>(responses, sortedPageable, translatorPage.getTotalElements());
        });
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Translator translator = getTranslatorOrThrow(id);
        orderRepo.findByTranslator_Id(id).forEach(order -> order.setTranslator(null));
        translatorRepo.delete(translator);
        cacheManager.invalidate(Translator.class, Order.class);
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

        Map<Integer, TranslatorLanguage> existingLanguagesById = new HashMap<>();
        for (TranslatorLanguage translatorLanguage : translator.getTranslatorLanguages()) {
            existingLanguagesById.put(translatorLanguage.getLanguage().getId(), translatorLanguage);
        }

        Set<Integer> requestedLanguageIds = new HashSet<>();
        for (TranslatorLanguageRequest languageRequest : languageRequests) {
            if (languageRequest.getLanguageId() != null) {
                requestedLanguageIds.add(languageRequest.getLanguageId());
            }
        }

        translator.getTranslatorLanguages().removeIf(
                translatorLanguage -> !requestedLanguageIds.contains(
                        translatorLanguage.getLanguage().getId()
                )
        );

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

            TranslatorLanguage translatorLanguage = existingLanguagesById.get(languageId);
            if (translatorLanguage == null) {
                translatorLanguage = new TranslatorLanguage();
                translatorLanguage.setTranslator(translator);
                translatorLanguage.setLanguage(language);
                translator.getTranslatorLanguages().add(translatorLanguage);
            }
            translatorLanguage.setProficiencyLevel(languageRequest.getProficiencyLevel());
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

    private Pageable withDefaultSort(Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            return pageable;
        }
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "id")
        );
    }
}
