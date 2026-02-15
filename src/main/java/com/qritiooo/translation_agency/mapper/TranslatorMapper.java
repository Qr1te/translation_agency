package com.qritiooo.translation_agency.mapper;

import com.qritiooo.translation_agency.dto.TranslatorDto;
import com.qritiooo.translation_agency.model.Language;
import com.qritiooo.translation_agency.model.Translator;
import lombok.experimental.UtilityClass;

import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class TranslatorMapper {

    public static TranslatorDto toDto(Translator t) {
        Set<Integer> ids = t.getLanguages().stream()
                .map(Language::getId)
                .collect(Collectors.toSet());

        return new TranslatorDto(t.getId(), t.getFullName(), t.getRatePerPage(), ids);
    }

    public static void updateEntity(Translator t, TranslatorDto dto) {
        t.setFullName(dto.getFullName());
        t.setRatePerPage(dto.getRatePerPage());
    }
}
