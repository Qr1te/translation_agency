package com.qritiooo.translation_agency.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TranslatorRequest {
    private String fullName;
    private BigDecimal ratePerPage;
    private Set<Integer> languageIds;
}
