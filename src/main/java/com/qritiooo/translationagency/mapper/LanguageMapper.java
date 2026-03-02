package com.qritiooo.translationagency.mapper;

import com.qritiooo.translationagency.dto.request.LanguageRequest;
import com.qritiooo.translationagency.dto.response.LanguageResponse;
import com.qritiooo.translationagency.model.Language;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LanguageMapper {

    public static LanguageResponse toResponse(Language l) {
        return new LanguageResponse(l.getId(), l.getCode(), l.getName());
    }

    public static void updateEntity(Language l, LanguageRequest request) {
        l.setCode(request.getCode());
        l.setName(request.getName());
    }

    public static void patchEntity(Language l, LanguageRequest request) {
        if (request.getCode() != null) {
            l.setCode(request.getCode());
        }
        if (request.getName() != null) {
            l.setName(request.getName());
        }
    }
}

