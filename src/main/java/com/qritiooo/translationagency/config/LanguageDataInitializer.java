package com.qritiooo.translationagency.config;

import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.repository.LanguageRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class LanguageDataInitializer implements ApplicationRunner {

    private final LanguageRepository languageRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        List<Language> missingLanguages = new ArrayList<>();

        for (Language language : defaultLanguages()) {
            if (languageRepository.findByCodeIgnoreCase(language.getCode()).isEmpty()) {
                missingLanguages.add(language);
            }
        }

        if (missingLanguages.isEmpty()) {
            return;
        }

        languageRepository.saveAll(missingLanguages);
        log.info("Added {} missing languages to the database", missingLanguages.size());
    }

    private List<Language> defaultLanguages() {
        return List.of(
                new Language(null, "EN", "English"),
                new Language(null, "RU", "Russian"),
                new Language(null, "DE", "German"),
                new Language(null, "FR", "French"),
                new Language(null, "IT", "Italian"),
                new Language(null, "SP", "Spanish"),
                new Language(null, "PL", "Polish"),
                new Language(null, "CN", "Chinese")
        );
    }
}
