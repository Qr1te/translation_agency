package com.qritiooo.translationagency.service;

import com.qritiooo.translationagency.dto.response.LanguageResponse;
import java.util.List;

public interface LanguageService {
    LanguageResponse getByCode(String code);

    List<LanguageResponse> getAll();
}

