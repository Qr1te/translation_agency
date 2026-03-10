package com.qritiooo.translationagency.dto.request;

import com.qritiooo.translationagency.model.LanguageProficiencyLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TranslatorLanguageRequest {
    private Integer languageId;
    private LanguageProficiencyLevel proficiencyLevel;
}
