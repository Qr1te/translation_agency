package com.qritiooo.translationagency.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qritiooo.translationagency.cache.CacheKey;
import com.qritiooo.translationagency.cache.CacheManager;
import com.qritiooo.translationagency.dto.request.TranslatorLanguageRequest;
import com.qritiooo.translationagency.dto.request.TranslatorRequest;
import com.qritiooo.translationagency.dto.response.TranslatorResponse;
import com.qritiooo.translationagency.exception.BadRequestException;
import com.qritiooo.translationagency.exception.NotFoundException;
import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.model.LanguageProficiencyLevel;
import com.qritiooo.translationagency.model.Order;
import com.qritiooo.translationagency.model.Translator;
import com.qritiooo.translationagency.repository.LanguageRepository;
import com.qritiooo.translationagency.repository.OrderRepository;
import com.qritiooo.translationagency.repository.TranslatorRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TranslatorServiceImplTest {

    @Mock
    private TranslatorRepository translatorRepository;

    @Mock
    private LanguageRepository languageRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private TranslatorServiceImpl translatorService;

    @Test
    void create_ShouldSaveTranslator_WhenLanguagesAreValid() {
        TranslatorRequest request = new TranslatorRequest(
                "Ivan",
                "Petrov",
                new BigDecimal("20.00"),
                List.of(new TranslatorLanguageRequest(1, LanguageProficiencyLevel.ADVANCED))
        );
        Language language = new Language(1, "EN", "English");
        when(languageRepository.findById(1)).thenReturn(Optional.of(language));
        when(translatorRepository.save(any(Translator.class))).thenAnswer(invocation -> {
            Translator translator = invocation.getArgument(0);
            translator.setId(1);
            return translator;
        });

        TranslatorResponse response = translatorService.create(request);

        assertEquals(1, response.getId());
        assertEquals(1, response.getLanguages().size());
        verify(cacheManager).invalidate(Translator.class, Order.class);
    }

    @Test
    void update_ShouldThrowNotFound_WhenTranslatorMissing() {
        TranslatorRequest request = new TranslatorRequest(
                "Ivan",
                "Petrov",
                new BigDecimal("20.00"),
                List.of(new TranslatorLanguageRequest(1, LanguageProficiencyLevel.ADVANCED))
        );
        when(translatorRepository.findById(77)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> translatorService.update(77, request));
    }

    @Test
    void patch_ShouldNotSyncLanguages_WhenLanguagesIsNull() {
        Translator translator = new Translator();
        translator.setId(3);
        translator.setFirstName("A");
        translator.setLastName("B");
        translator.setRatePerPage(new BigDecimal("10"));
        Language language = new Language(1, "EN", "English");
        translator.getTranslatorLanguages().add(
                new com.qritiooo.translationagency.model.TranslatorLanguage(
                        null,
                        translator,
                        language,
                        LanguageProficiencyLevel.NATIVE
                )
        );
        TranslatorRequest request = new TranslatorRequest("C", null, null, null);
        when(translatorRepository.findById(3)).thenReturn(Optional.of(translator));
        when(translatorRepository.save(translator)).thenReturn(translator);

        TranslatorResponse response = translatorService.patch(3, request);

        assertEquals("C", response.getFirstName());
        assertEquals(1, response.getLanguages().size());
        verify(languageRepository, never()).findById(any());
    }

    @Test
    void create_ShouldThrowBadRequest_WhenDuplicateLanguageIds() {
        TranslatorRequest request = new TranslatorRequest(
                "Ivan",
                "Petrov",
                new BigDecimal("20.00"),
                List.of(
                        new TranslatorLanguageRequest(1, LanguageProficiencyLevel.ADVANCED),
                        new TranslatorLanguageRequest(1, LanguageProficiencyLevel.NATIVE)
                )
        );

        assertThrows(BadRequestException.class, () -> translatorService.create(request));
        verify(translatorRepository, never()).save(any(Translator.class));
    }

    @Test
    void create_ShouldThrowBadRequest_WhenLanguageIdMissing() {
        TranslatorRequest request = new TranslatorRequest(
                "Ivan",
                "Petrov",
                new BigDecimal("20.00"),
                List.of(new TranslatorLanguageRequest(null, LanguageProficiencyLevel.ADVANCED))
        );

        assertThrows(BadRequestException.class, () -> translatorService.create(request));
    }

    @Test
    void create_ShouldThrowBadRequest_WhenProficiencyMissing() {
        TranslatorRequest request = new TranslatorRequest(
                "Ivan",
                "Petrov",
                new BigDecimal("20.00"),
                List.of(new TranslatorLanguageRequest(1, null))
        );

        assertThrows(BadRequestException.class, () -> translatorService.create(request));
    }

    @Test
    void getById_ShouldReturnTranslator_WhenFound() {
        Translator translator = new Translator();
        translator.setId(8);
        translator.setFirstName("N");
        translator.setLastName("S");
        translator.setRatePerPage(new BigDecimal("15"));
        when(translatorRepository.findById(8)).thenReturn(Optional.of(translator));

        TranslatorResponse response = translatorService.getById(8);

        assertEquals(8, response.getId());
    }

    @Test
    @SuppressWarnings("unchecked")
    void getAll_ShouldReturnFromCacheSupplier() {
        Translator first = new Translator();
        first.setId(1);
        first.setFirstName("A");
        first.setLastName("B");
        first.setRatePerPage(new BigDecimal("10"));
        when(translatorRepository.findAll()).thenReturn(List.of(first));
        when(cacheManager.computeIfAbsent(any(CacheKey.class), any(Supplier.class)))
                .thenAnswer(invocation -> {
                    Supplier<List<TranslatorResponse>> supplier = invocation.getArgument(1);
                    return supplier.get();
                });

        List<TranslatorResponse> result = translatorService.getAll();

        assertEquals(1, result.size());
    }

    @Test
    void delete_ShouldDetachOrdersAndDeleteTranslator() {
        Translator translator = new Translator();
        translator.setId(5);
        Order order = new Order();
        order.setTranslator(translator);
        when(translatorRepository.findById(5)).thenReturn(Optional.of(translator));
        when(orderRepository.findByTranslator_Id(5)).thenReturn(List.of(order));

        translatorService.delete(5);

        assertNull(order.getTranslator());
        verify(translatorRepository).delete(translator);
        verify(cacheManager).invalidate(Translator.class, Order.class);
    }
}

