package com.qritiooo.translation_agency.mapper;

import com.qritiooo.translation_agency.dto.request.LanguageRequest;
import com.qritiooo.translation_agency.dto.response.LanguageResponse;
import com.qritiooo.translation_agency.model.Language;
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
}
