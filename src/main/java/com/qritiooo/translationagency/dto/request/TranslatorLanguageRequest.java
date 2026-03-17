package com.qritiooo.translationagency.dto.request;

import com.qritiooo.translationagency.model.LanguageProficiencyLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Language and proficiency item for a translator")
public class TranslatorLanguageRequest {
    @NotNull(message = "languageId is required")
    @Positive(message = "languageId must be greater than zero")
    @Schema(description = "Language id", example = "1")
    private Integer languageId;

    @NotNull(message = "proficiencyLevel is required")
    @Schema(description = "Translator proficiency level")
    private LanguageProficiencyLevel proficiencyLevel;
}
