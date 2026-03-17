package com.qritiooo.translationagency.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Translator patch request")
public class TranslatorPatchRequest {
    @Size(max = 100, message = "firstName must be at most 100 characters")
    @Schema(description = "Translator first name", example = "Ivan")
    private String firstName;

    @Size(max = 100, message = "lastName must be at most 100 characters")
    @Schema(description = "Translator last name", example = "Petrov")
    private String lastName;

    @DecimalMin(value = "0.0", inclusive = false, message = "ratePerPage must be positive")
    @Schema(description = "Translator rate per page", example = "15.50")
    private BigDecimal ratePerPage;

    @Valid
    @Schema(description = "Translator language proficiencies")
    private List<TranslatorLanguageRequest> languages;
}
