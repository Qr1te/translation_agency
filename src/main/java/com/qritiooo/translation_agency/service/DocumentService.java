package com.qritiooo.translation_agency.service;

import com.qritiooo.translation_agency.dto.request.DocumentRequest;
import com.qritiooo.translation_agency.dto.response.DocumentResponse;

import java.util.List;

public interface DocumentService {
    DocumentResponse create(DocumentRequest request);
    DocumentResponse update(Integer id, DocumentRequest request);
    DocumentResponse getById(Integer id);
    List<DocumentResponse> getAll(Integer orderId);
    void delete(Integer id);
}
