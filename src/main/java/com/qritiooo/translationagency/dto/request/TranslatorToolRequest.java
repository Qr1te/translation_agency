package com.qritiooo.translationagency.dto.request;

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
public class TranslatorToolRequest {
    private Integer toolId;
    private LocalDate licenseExpiryDate;
    private ProficiencyLevel proficiencyLevel;
}
