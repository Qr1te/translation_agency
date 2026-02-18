package com.qritiooo.translation_agency.mapper;

import com.qritiooo.translation_agency.dto.request.TranslatorRequest;
import com.qritiooo.translation_agency.dto.response.TranslatorResponse;
import com.qritiooo.translation_agency.model.Language;
import com.qritiooo.translation_agency.model.Translator;
import lombok.experimental.UtilityClass;

import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class TranslatorMapper {

    public static TranslatorResponse toResponse(Translator t) {
        Set<Integer> ids = t.getLanguages().stream()
                .map(Language::getId)
                .collect(Collectors.toSet());

        return new TranslatorResponse(t.getId(), t.getFullName(), t.getRatePerPage(), ids);
    }

    public static void updateEntity(Translator t, TranslatorRequest request) {
        t.setFullName(request.getFullName());
        t.setRatePerPage(request.getRatePerPage());
    }
}
