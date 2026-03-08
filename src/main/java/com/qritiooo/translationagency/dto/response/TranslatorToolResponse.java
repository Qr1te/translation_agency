package com.qritiooo.translationagency.dto.response;

import com.qritiooo.translationagency.model.ProficiencyLevel;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TranslatorToolResponse {
    private Integer toolId;
    private String toolName;
    private LocalDate licenseExpiryDate;
    private ProficiencyLevel proficiencyLevel;
}
