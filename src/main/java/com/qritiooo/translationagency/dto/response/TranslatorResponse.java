package com.qritiooo.translationagency.dto.response;

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
public class TranslatorResponse {
    private Integer id;
    private String firstName;
    private String lastName;
    private BigDecimal ratePerPage;
    private List<TranslatorLanguageResponse> languages;
}

