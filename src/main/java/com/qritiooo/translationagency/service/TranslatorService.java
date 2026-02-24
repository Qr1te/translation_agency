package com.qritiooo.translationagency.service;

import com.qritiooo.translationagency.dto.request.TranslatorRequest;
import com.qritiooo.translationagency.dto.response.TranslatorResponse;
import java.util.List;

public interface TranslatorService {
    TranslatorResponse create(TranslatorRequest request);

    TranslatorResponse update(Integer id, TranslatorRequest request);

    TranslatorResponse getById(Integer id);

    List<TranslatorResponse> getAll();

    void delete(Integer id);
}

