package com.qritiooo.translationagency.service;

import com.qritiooo.translationagency.dto.request.DocumentRequest;
import com.qritiooo.translationagency.dto.response.DocumentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DocumentService {
    DocumentResponse create(DocumentRequest request);

    DocumentResponse update(Integer id, DocumentRequest request);

    DocumentResponse patch(Integer id, DocumentRequest request);

    DocumentResponse getById(Integer id);

    Page<DocumentResponse> getAll(Integer orderId, Pageable pageable);

    void delete(Integer id);
}

