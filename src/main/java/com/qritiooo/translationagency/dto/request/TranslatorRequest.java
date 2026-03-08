package com.qritiooo.translationagency.dto.request;

import com.qritiooo.translationagency.model.Language;
import java.math.BigDecimal;
import java.util.List;
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
    private Set<Language> languages;
    private List<TranslatorToolRequest> tools;
}

