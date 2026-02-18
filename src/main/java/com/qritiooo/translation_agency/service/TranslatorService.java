package com.qritiooo.translation_agency.service;

import com.qritiooo.translation_agency.dto.request.TranslatorRequest;
import com.qritiooo.translation_agency.dto.response.TranslatorResponse;

import java.util.List;

public interface TranslatorService {
    TranslatorResponse create(TranslatorRequest request);
    TranslatorResponse update(Integer id, TranslatorRequest request);
    TranslatorResponse getById(Integer id);
    List<TranslatorResponse> getAll();
    void delete(Integer id);
}
