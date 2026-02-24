package com.qritiooo.translationagency.service;

import com.qritiooo.translationagency.dto.request.DocumentRequest;
import com.qritiooo.translationagency.dto.response.DocumentResponse;
import java.util.List;

public interface DocumentService {
    DocumentResponse create(DocumentRequest request);

    DocumentResponse update(Integer id, DocumentRequest request);

    DocumentResponse getById(Integer id);

    List<DocumentResponse> getAll(Integer orderId);

    void delete(Integer id);
}

