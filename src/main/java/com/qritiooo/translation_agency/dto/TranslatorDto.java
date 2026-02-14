package com.qritiooo.translation_agency.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TranslatorDto {
    private Integer id;
    private String fullName;
    private BigDecimal ratePerPage;
    private Set<Integer> languageIds;
}
