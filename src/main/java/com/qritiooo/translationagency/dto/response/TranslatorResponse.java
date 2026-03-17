package com.qritiooo.translationagency.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Translator response DTO")
public class TranslatorResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private BigDecimal ratePerPage;
    private List<TranslatorLanguageResponse> languages;
}

