package com.qritiooo.translationagency.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qritiooo.translationagency.cache.CacheKey;
import com.qritiooo.translationagency.cache.CacheManager;
import com.qritiooo.translationagency.dto.response.LanguageResponse;
import com.qritiooo.translationagency.exception.NotFoundException;
import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.repository.LanguageRepository;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LanguageServiceImplTest {

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private LanguageServiceImpl languageService;

    @Test
    void getByCode_ShouldNormalizeAliasAndReturnLanguage() {
        Language language = new Language(1, "EN", "English");
        when(languageRepository.findByCodeIgnoreCase("EN")).thenReturn(Optional.of(language));

        LanguageResponse response = languageService.getByCode("english");

        assertEquals(1, response.getId());
        assertEquals("EN", response.getCode());
        verify(languageRepository).findByCodeIgnoreCase("EN");
    }

    @Test
    void getByCode_ShouldThrowNotFound_WhenLanguageMissing() {
        when(languageRepository.findByCodeIgnoreCase(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> languageService.getByCode("xx"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAll_ShouldReturnMappedLanguagesFromCacheSupplier() {
        Language first = new Language(1, "EN", "English");
        Language second = new Language(2, "RU", "Russian");
        when(languageRepository.findAll()).thenReturn(List.of(first, second));
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<List<LanguageResponse>> supplier = invocation.getArgument(1);
                    return supplier.get();
                });

        List<LanguageResponse> result = languageService.getAll();

        assertEquals(2, result.size());
        assertEquals("EN", result.getFirst().getCode());
        verify(cacheManager).computeIfAbsent(any(CacheKey.class), any(Supplier.class));
    }
}
