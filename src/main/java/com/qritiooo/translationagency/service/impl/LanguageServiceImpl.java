package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.config.CacheNames;
import com.qritiooo.translationagency.dto.response.LanguageResponse;
import com.qritiooo.translationagency.exception.NotFoundException;
import com.qritiooo.translationagency.mapper.LanguageMapper;
import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.repository.LanguageRepository;
import com.qritiooo.translationagency.service.LanguageService;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = CacheNames.LANGUAGES_ALL)
public class LanguageServiceImpl implements LanguageService {

    private final LanguageRepository languageRepository;

    @Override
    public LanguageResponse getByCode(String code) {
        String normalizedCode = normalizeCode(code);
        Language language = languageRepository.findByCodeIgnoreCase(normalizedCode).orElseThrow(
                () -> new NotFoundException("Language not found with code: " + code)
        );
        return LanguageMapper.toResponse(language);
    }

    @Override
    @Cacheable(sync = true)
    public List<LanguageResponse> getAll() {
        return languageRepository.findAll().stream().map(LanguageMapper::toResponse).toList();
    }

    private String normalizeCode(String code) {
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

