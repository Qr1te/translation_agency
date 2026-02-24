package com.qritiooo.translationagency.dto.request;

import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TranslatorRequest {
    private String fullName;
    private BigDecimal ratePerPage;
    private Set<Integer> languageIds;
}

