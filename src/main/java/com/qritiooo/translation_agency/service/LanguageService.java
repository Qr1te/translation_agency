package com.qritiooo.translation_agency.service;

import com.qritiooo.translation_agency.dto.request.LanguageRequest;
import com.qritiooo.translation_agency.dto.response.LanguageResponse;

import java.util.List;

public interface LanguageService {
    LanguageResponse create(LanguageRequest request);
    LanguageResponse update(Integer id, LanguageRequest request);
    LanguageResponse getById(Integer id);
    List<LanguageResponse> getAll();
    void delete(Integer id);
}
