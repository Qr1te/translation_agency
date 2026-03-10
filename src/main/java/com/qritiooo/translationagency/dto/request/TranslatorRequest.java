package com.qritiooo.translationagency.dto.request;

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
public class TranslatorRequest {
    private String firstName;
    private String lastName;
    private BigDecimal ratePerPage;
    private List<TranslatorLanguageRequest> languages;
}

