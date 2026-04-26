package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.cache.CacheKey;
import com.qritiooo.translationagency.cache.CacheManager;
import com.qritiooo.translationagency.dto.request.LanguageRequest;
import com.qritiooo.translationagency.dto.response.LanguageResponse;
import com.qritiooo.translationagency.exception.ConflictException;
import com.qritiooo.translationagency.exception.NotFoundException;
import com.qritiooo.translationagency.mapper.LanguageMapper;
import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.model.Order;
import com.qritiooo.translationagency.model.Translator;
import com.qritiooo.translationagency.repository.LanguageRepository;
import com.qritiooo.translationagency.repository.OrderRepository;
import com.qritiooo.translationagency.repository.TranslatorRepository;
import com.qritiooo.translationagency.service.LanguageService;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;
    private final OrderRepository orderRepository;
    private final TranslatorRepository translatorRepository;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    public LanguageResponse create(LanguageRequest request) {
        Language language = new Language();
        LanguageMapper.updateEntity(language, request);
        return saveAndMap(language);
    }

    @Override
    @Transactional
    public LanguageResponse update(Integer id, LanguageRequest request) {
        Language language = getLanguageOrThrow(id);
        LanguageMapper.updateEntity(language, request);
        return saveAndMap(language);
    }

    @Override
    @Transactional
    public LanguageResponse patch(Integer id, LanguageRequest request) {
        Language language = getLanguageOrThrow(id);
        LanguageMapper.patchEntity(language, request);
        return saveAndMap(language);
    }

    @Override
    public LanguageResponse getById(Integer id) {
        return LanguageMapper.toResponse(getLanguageOrThrow(id));
    }

    @Override
    public LanguageResponse getByCode(String code) {
        String normalizedCode = normalizeLookupCode(code);
        Language language = languageRepository.findByCodeIgnoreCase(normalizedCode).orElseThrow(
                () -> new NotFoundException("Language not found with code: " + code)
        );
        return LanguageMapper.toResponse(language);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LanguageResponse> getAll(Pageable pageable) {
        Pageable sortedPageable = withDefaultSort(pageable);
        CacheKey key = new CacheKey(
                Language.class,
                "getAll",
                sortedPageable.getPageNumber(),
                sortedPageable.getPageSize(),
                sortedPageable.getSort().toString()
        );
        return cacheManager.computeIfAbsent(
                key,
                () -> languageRepository.findAll(sortedPageable).map(LanguageMapper::toResponse)
        );
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        final Language language = getLanguageOrThrow(id);

        List<Order> relatedOrders = orderRepository
                .findDistinctBySourceLanguage_IdOrTargetLanguage_Id(id, id);
        for (Order order : relatedOrders) {
            if (order.getSourceLanguage() != null && id.equals(order.getSourceLanguage().getId())) {
                order.setSourceLanguage(null);
            }
            if (order.getTargetLanguage() != null && id.equals(order.getTargetLanguage().getId())) {
                order.setTargetLanguage(null);
            }
        }

        List<Translator> relatedTranslators = translatorRepository
                .findDistinctByTranslatorLanguages_Language_Id(id);
        for (Translator translator : relatedTranslators) {
            translator.getTranslatorLanguages().removeIf(
                    translatorLanguage -> translatorLanguage.getLanguage() != null
                            && id.equals(translatorLanguage.getLanguage().getId())
            );
        }

        orderRepository.flush();
        translatorRepository.flush();
        languageRepository.delete(language);
        invalidateCaches();
    }

    private Language getLanguageOrThrow(Integer id) {
        return languageRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Language not found with id: " + id)
        );
    }

    private LanguageResponse saveAndMap(Language language) {
        normalizeForPersistence(language);
        ensureCodeIsUnique(language);
        ensureNameIsUnique(language);
        Language savedLanguage = languageRepository.saveAndFlush(language);
        invalidateCaches();
        return LanguageMapper.toResponse(savedLanguage);
    }

    private void normalizeForPersistence(Language language) {
        if (language.getCode() != null) {
            language.setCode(language.getCode().trim().toUpperCase(Locale.ROOT));
        }
        if (language.getName() != null) {
            language.setName(language.getName().trim());
        }
    }

    private void ensureCodeIsUnique(Language language) {
        String code = language.getCode();
        boolean exists = language.getId() == null
                ? languageRepository.existsByCodeIgnoreCase(code)
                : languageRepository.existsByCodeIgnoreCaseAndIdNot(code, language.getId());

        if (exists) {
            throw new ConflictException("Language with code already exists: " + code);
        }
    }

    private void ensureNameIsUnique(Language language) {
        String name = language.getName();
        boolean exists = language.getId() == null
                ? languageRepository.existsByNameIgnoreCase(name)
                : languageRepository.existsByNameIgnoreCaseAndIdNot(name, language.getId());

        if (exists) {
            throw new ConflictException("Language with name already exists: " + name);
        }
    }

    private void invalidateCaches() {
        cacheManager.invalidate(Language.class, Order.class, Translator.class);
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

    private String normalizeLookupCode(String code) {
        if (code == null) {
            return null;
        }
        return switch (code.trim().toUpperCase(Locale.ROOT)) {
            case "ENGLISH" -> "EN";
            case "RUSSIAN" -> "RU";
            case "GERMAN" -> "DE";
            case "FRENCH" -> "FR";
            case "ITALIAN" -> "IT";
            case "SPANISH" -> "SP";
            case "POLISH" -> "PL";
            case "CHINESE" -> "CN";
            default -> code.trim().toUpperCase(Locale.ROOT);
        };
    }
}

