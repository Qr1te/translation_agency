package com.qritiooo.translationagency.service;

import com.qritiooo.translationagency.dto.request.LanguageRequest;
import com.qritiooo.translationagency.dto.response.LanguageResponse;
import java.util.List;

public interface LanguageService {
    LanguageResponse create(LanguageRequest request);

    LanguageResponse update(Integer id, LanguageRequest request);

    LanguageResponse getById(Integer id);

    List<LanguageResponse> getAll();

    void delete(Integer id);
}

