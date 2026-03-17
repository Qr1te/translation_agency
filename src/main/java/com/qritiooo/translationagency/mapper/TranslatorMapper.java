package com.qritiooo.translationagency.mapper;

import com.qritiooo.translationagency.dto.request.TranslatorRequest;
import com.qritiooo.translationagency.dto.response.TranslatorLanguageResponse;
import com.qritiooo.translationagency.dto.response.TranslatorResponse;
import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.model.Translator;
import com.qritiooo.translationagency.model.TranslatorLanguage;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TranslatorMapper {

    public static TranslatorResponse toResponse(Translator translator) {
        List<TranslatorLanguageResponse> languages = translator.getTranslatorLanguages()
                .stream()
                .map(TranslatorMapper::toLanguageResponse)
                .toList();

        return new TranslatorResponse(
                translator.getId(),
                translator.getFirstName(),
                translator.getLastName(),
                translator.getRatePerPage(),
                languages
        );
    }

    public static void updateEntity(Translator translator, TranslatorRequest request) {
        translator.setFirstName(request.getFirstName());
        translator.setLastName(request.getLastName());
        translator.setRatePerPage(request.getRatePerPage());
    }

    public static void patchEntity(Translator translator, TranslatorRequest request) {
        if (request.getFirstName() != null) {
            translator.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            translator.setLastName(request.getLastName());
        }
        if (request.getRatePerPage() != null) {
            translator.setRatePerPage(request.getRatePerPage());
        }
    }

    private static TranslatorLanguageResponse toLanguageResponse(
            TranslatorLanguage translatorLanguage
    ) {
        Language language = translatorLanguage.getLanguage();
        return new TranslatorLanguageResponse(
                language.getId(),
                language.getCode(),
                language.getName(),
                translatorLanguage.getProficiencyLevel()
        );
    }
}
