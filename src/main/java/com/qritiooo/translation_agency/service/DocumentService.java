package com.qritiooo.translation_agency.service;

import com.qritiooo.translation_agency.dto.DocumentDto;

import java.util.List;

public interface DocumentService {
    DocumentDto create(DocumentDto dto);
    DocumentDto update(Integer id, DocumentDto dto);
    DocumentDto getById(Integer id);
    List<DocumentDto> getAll(Integer orderId);
    void delete(Integer id);
}
