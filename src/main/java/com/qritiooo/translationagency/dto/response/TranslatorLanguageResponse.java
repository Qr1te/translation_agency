package com.qritiooo.translationagency.dto.response;

import com.qritiooo.translationagency.model.LanguageProficiencyLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Translator language response DTO")
public class TranslatorLanguageResponse {
    private Integer languageId;
    private String code;
    private String name;
    private LanguageProficiencyLevel proficiencyLevel;
}
