package com.qritiooo.translationagency.dto.response;

import com.qritiooo.translationagency.model.LanguageProficiencyLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TranslatorLanguageResponse {
    private Integer languageId;
    private String code;
    private String name;
    private LanguageProficiencyLevel proficiencyLevel;
}
