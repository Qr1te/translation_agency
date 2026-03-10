package com.qritiooo.translationagency.mapper;

import com.qritiooo.translationagency.dto.response.LanguageResponse;
import com.qritiooo.translationagency.model.Language;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LanguageMapper {

    public static LanguageResponse toResponse(Language language) {
        return new LanguageResponse(language.getId(), language.getCode(), language.getName());
    }
}

