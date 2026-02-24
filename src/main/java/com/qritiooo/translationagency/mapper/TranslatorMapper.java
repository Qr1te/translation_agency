package com.qritiooo.translationagency.mapper;

import com.qritiooo.translationagency.dto.request.TranslatorRequest;
import com.qritiooo.translationagency.dto.response.TranslatorResponse;
import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.model.Translator;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

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

