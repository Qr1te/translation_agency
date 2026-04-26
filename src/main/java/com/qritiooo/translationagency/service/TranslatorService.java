package com.qritiooo.translationagency.service;

import com.qritiooo.translationagency.dto.request.TranslatorRequest;
import com.qritiooo.translationagency.dto.response.TranslatorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TranslatorService {
    TranslatorResponse create(TranslatorRequest request);

    TranslatorResponse update(Integer id, TranslatorRequest request);

    TranslatorResponse patch(Integer id, TranslatorRequest request);

    TranslatorResponse getById(Integer id);

    Page<TranslatorResponse> getAll(Pageable pageable);

    void delete(Integer id);
}

