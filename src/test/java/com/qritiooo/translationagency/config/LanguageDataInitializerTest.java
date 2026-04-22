package com.qritiooo.translationagency.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.repository.LanguageRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.DefaultApplicationArguments;

@ExtendWith(MockitoExtension.class)
class LanguageDataInitializerTest {

    @Mock
    private LanguageRepository languageRepository;

    @InjectMocks
    private LanguageDataInitializer initializer;

    @Test
    void run_ShouldInsertMissingLanguages() throws Exception {
        when(languageRepository.findByCodeIgnoreCase("EN"))
                .thenReturn(Optional.of(new Language(1, "EN", "English")));
        when(languageRepository.findByCodeIgnoreCase("RU"))
                .thenReturn(Optional.of(new Language(2, "RU", "Russian")));
        when(languageRepository.findByCodeIgnoreCase("DE")).thenReturn(Optional.empty());
        when(languageRepository.findByCodeIgnoreCase("FR")).thenReturn(Optional.empty());
        when(languageRepository.findByCodeIgnoreCase("IT")).thenReturn(Optional.empty());
        when(languageRepository.findByCodeIgnoreCase("SP")).thenReturn(Optional.empty());
        when(languageRepository.findByCodeIgnoreCase("PL")).thenReturn(Optional.empty());
        when(languageRepository.findByCodeIgnoreCase("CN")).thenReturn(Optional.empty());

        initializer.run(new DefaultApplicationArguments(new String[0]));

        ArgumentCaptor<List<Language>> languagesCaptor = ArgumentCaptor.forClass(List.class);
        verify(languageRepository).saveAll(languagesCaptor.capture());

        List<Language> savedLanguages = languagesCaptor.getValue();
        assertEquals(6, savedLanguages.size());
        assertEquals(List.of("DE", "FR", "IT", "SP", "PL", "CN"),
                savedLanguages.stream().map(Language::getCode).toList());
    }

    @Test
    void run_ShouldSkipInsertWhenAllLanguagesAlreadyExist() throws Exception {
        for (String code : List.of("EN", "RU", "DE", "FR", "IT", "SP", "PL", "CN")) {
            when(languageRepository.findByCodeIgnoreCase(code))
                    .thenReturn(Optional.of(new Language(1, code, code)));
        }

        initializer.run(new DefaultApplicationArguments(new String[0]));

        verify(languageRepository, never()).saveAll(anyList());
    }
}
