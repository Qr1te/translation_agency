package com.qritiooo.translation_agency.mapper;

import com.qritiooo.translation_agency.dto.LanguageDto;
import com.qritiooo.translation_agency.model.Language;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LanguageMapper {

    public static LanguageDto toDto(Language l) {
        return new LanguageDto(l.getId(), l.getCode(), l.getName());
    }

    public static void updateEntity(Language l, LanguageDto dto) {
        l.setCode(dto.getCode());
        l.setName(dto.getName());
    }
}
