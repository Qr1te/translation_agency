package com.qritiooo.translationagency.mapper;

import com.qritiooo.translationagency.dto.request.TranslatorRequest;
import com.qritiooo.translationagency.dto.response.TranslatorResponse;
import com.qritiooo.translationagency.dto.response.TranslatorToolResponse;
import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.model.Translator;
import com.qritiooo.translationagency.model.TranslatorTool;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TranslatorMapper {

    public static TranslatorResponse toResponse(Translator t) {
        Set<Language> languages = new HashSet<>(t.getLanguages());
        List<TranslatorToolResponse> tools = t
                .getTranslatorTools()
                .stream()
                .map(TranslatorMapper::toToolResponse)
                .toList();
        return new TranslatorResponse(
                t.getId(),
                t.getFullName(),
                t.getRatePerPage(),
                languages,
                tools
        );
    }

    public static void updateEntity(Translator t, TranslatorRequest request) {
        t.setFullName(request.getFullName());
        t.setRatePerPage(request.getRatePerPage());
    }

    public static void patchEntity(Translator t, TranslatorRequest request) {
        if (request.getFullName() != null) {
            t.setFullName(request.getFullName());
        }
        if (request.getRatePerPage() != null) {
            t.setRatePerPage(request.getRatePerPage());
        }
    }

    private static TranslatorToolResponse toToolResponse(
            TranslatorTool translatorTool
    ) {
        return new TranslatorToolResponse(
                translatorTool.getTool().getId(),
                translatorTool.getTool().getName(),
                translatorTool.getLicenseExpiryDate(),
                translatorTool.getProficiencyLevel()
        );
    }
}

