package com.qritiooo.translationagency.mapper;

import com.qritiooo.translationagency.dto.request.LanguageRequest;
import com.qritiooo.translationagency.dto.response.LanguageResponse;
import com.qritiooo.translationagency.model.Language;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LanguageMapper {

    public static LanguageResponse toResponse(Language language) {
        return new LanguageResponse(language.getId(), language.getCode(), language.getName());
    }

    public static void updateEntity(Language language, LanguageRequest request) {
        language.setCode(request.getCode());
        language.setName(request.getName());
    }

    public static void patchEntity(Language language, LanguageRequest request) {
        if (request.getCode() != null) {
            language.setCode(request.getCode());
        }
        if (request.getName() != null) {
            language.setName(request.getName());
        }
    }
}

