package com.qritiooo.translationagency.service;

import com.qritiooo.translationagency.dto.request.LanguageRequest;
import com.qritiooo.translationagency.dto.response.LanguageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LanguageService {
    LanguageResponse create(LanguageRequest request);

    LanguageResponse update(Integer id, LanguageRequest request);

    LanguageResponse patch(Integer id, LanguageRequest request);

    LanguageResponse getById(Integer id);

    LanguageResponse getByCode(String code);

    Page<LanguageResponse> getAll(Pageable pageable);

    void delete(Integer id);
}

