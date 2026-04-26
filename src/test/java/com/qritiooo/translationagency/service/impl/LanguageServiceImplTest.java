package com.qritiooo.translationagency.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qritiooo.translationagency.cache.CacheKey;
import com.qritiooo.translationagency.cache.CacheManager;
import com.qritiooo.translationagency.dto.request.LanguageRequest;
import com.qritiooo.translationagency.dto.response.LanguageResponse;
import com.qritiooo.translationagency.exception.ConflictException;
import com.qritiooo.translationagency.exception.NotFoundException;
import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.model.Order;
import com.qritiooo.translationagency.model.Translator;
import com.qritiooo.translationagency.model.TranslatorLanguage;
import com.qritiooo.translationagency.repository.LanguageRepository;
import com.qritiooo.translationagency.repository.OrderRepository;
import com.qritiooo.translationagency.repository.TranslatorRepository;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class LanguageServiceImplTest {

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private TranslatorRepository translatorRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private LanguageServiceImpl languageService;

    @Test
    void create_ShouldNormalizeFieldsAndInvalidateRelatedCaches() {
        LanguageRequest request = new LanguageRequest(" en ", " English ");
        when(languageRepository.existsByCodeIgnoreCase("EN")).thenReturn(false);
        when(languageRepository.existsByNameIgnoreCase("English")).thenReturn(false);
        when(languageRepository.saveAndFlush(any(Language.class)))
                .thenAnswer(invocation -> {
                    Language language = invocation.getArgument(0);
                    language.setId(1);
                    return language;
                });

        LanguageResponse response = languageService.create(request);

        assertEquals(1, response.getId());
        assertEquals("EN", response.getCode());
        assertEquals("English", response.getName());
        verify(cacheManager).invalidate(Language.class, Order.class, Translator.class);
    }

    @Test
    void create_ShouldThrowConflict_WhenCodeAlreadyExists() {
        LanguageRequest request = new LanguageRequest("EN", "English");
        when(languageRepository.existsByCodeIgnoreCase("EN")).thenReturn(true);

        assertThrows(ConflictException.class, () -> languageService.create(request));

        verify(languageRepository, never()).saveAndFlush(any(Language.class));
    }

    @Test
    void patch_ShouldUpdateOnlyProvidedFields() {
        Language language = new Language(1, "EN", "English");
        LanguageRequest request = new LanguageRequest(null, "English US");
        when(languageRepository.findById(1)).thenReturn(Optional.of(language));
        when(languageRepository.existsByCodeIgnoreCaseAndIdNot("EN", 1)).thenReturn(false);
        when(languageRepository.existsByNameIgnoreCaseAndIdNot("English US", 1)).thenReturn(false);
        when(languageRepository.saveAndFlush(language)).thenReturn(language);

        LanguageResponse response = languageService.patch(1, request);

        assertEquals("EN", response.getCode());
        assertEquals("English US", response.getName());
    }

    @Test
    void getById_ShouldReturnMappedLanguage() {
        Language language = new Language(1, "EN", "English");
        when(languageRepository.findById(1)).thenReturn(Optional.of(language));

        LanguageResponse response = languageService.getById(1);

        assertEquals(1, response.getId());
        assertEquals("English", response.getName());
    }

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
    void getByCode_ShouldNormalizeAllSupportedAliases() {
        when(languageRepository.findByCodeIgnoreCase(anyString()))
                .thenAnswer(invocation -> {
                    String code = invocation.getArgument(0);
                    return Optional.of(new Language(1, code, code));
                });

        assertEquals("EN", languageService.getByCode("english").getCode());
        assertEquals("RU", languageService.getByCode("russian").getCode());
        assertEquals("DE", languageService.getByCode("german").getCode());
        assertEquals("FR", languageService.getByCode("french").getCode());
        assertEquals("IT", languageService.getByCode("italian").getCode());
        assertEquals("SP", languageService.getByCode("spanish").getCode());
        assertEquals("PL", languageService.getByCode("polish").getCode());
        assertEquals("CN", languageService.getByCode("chinese").getCode());
        assertEquals("BY", languageService.getByCode(" by ").getCode());
    }

    @Test
    void getByCode_ShouldPassNullToRepository_WhenCodeIsNull() {
        when(languageRepository.findByCodeIgnoreCase(null)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> languageService.getByCode(null));
        verify(languageRepository).findByCodeIgnoreCase(null);
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAll_ShouldReturnPagedLanguagesFromCacheSupplier() {
        PageRequest pageable = PageRequest.of(0, 5, Sort.by("id"));
        Page<Language> page = new PageImpl<>(
                List.of(
                        new Language(1, "EN", "English"),
                        new Language(2, "RU", "Russian")
                ),
                pageable,
                2
        );
        when(languageRepository.findAll(pageable)).thenReturn(page);
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<Page<LanguageResponse>> supplier = invocation.getArgument(1);
                    return supplier.get();
                });

        Page<LanguageResponse> result = languageService.getAll(pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("EN", result.getContent().getFirst().getCode());
        verify(cacheManager).computeIfAbsent(any(CacheKey.class), any(Supplier.class));
    }

    @Test
    void delete_ShouldDetachOrdersAndTranslatorLanguagesBeforeDelete() {
        Language language = new Language(1, "EN", "English");
        Order order = new Order();
        order.setSourceLanguage(language);
        order.setTargetLanguage(language);

        Translator translator = new Translator();
        TranslatorLanguage translatorLanguage = new TranslatorLanguage();
        translatorLanguage.setLanguage(language);
        translator.getTranslatorLanguages().add(translatorLanguage);

        when(languageRepository.findById(1)).thenReturn(Optional.of(language));
        when(orderRepository.findDistinctBySourceLanguage_IdOrTargetLanguage_Id(1, 1))
                .thenReturn(List.of(order));
        when(translatorRepository.findDistinctByTranslatorLanguages_Language_Id(1))
                .thenReturn(List.of(translator));
        doNothing().when(orderRepository).flush();
        doNothing().when(translatorRepository).flush();

        languageService.delete(1);

        assertEquals(null, order.getSourceLanguage());
        assertEquals(null, order.getTargetLanguage());
        assertEquals(0, translator.getTranslatorLanguages().size());
        verify(languageRepository).delete(language);
        verify(cacheManager).invalidate(Language.class, Order.class, Translator.class);
    }

    @Test
    void update_ShouldThrowNotFound_WhenLanguageMissing() {
        when(languageRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(
                NotFoundException.class,
                () -> languageService.update(99, new LanguageRequest("BY", "Belarusian"))
        );
    }

    @Test
    void create_ShouldSendNormalizedLanguageToRepository() {
        LanguageRequest request = new LanguageRequest(" by ", " Belarusian ");
        when(languageRepository.existsByCodeIgnoreCase("BY")).thenReturn(false);
        when(languageRepository.existsByNameIgnoreCase("Belarusian")).thenReturn(false);
        when(languageRepository.saveAndFlush(any(Language.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        languageService.create(request);

        ArgumentCaptor<Language> captor = ArgumentCaptor.forClass(Language.class);
        verify(languageRepository).saveAndFlush(captor.capture());
        assertEquals("BY", captor.getValue().getCode());
        assertEquals("Belarusian", captor.getValue().getName());
    }
}

