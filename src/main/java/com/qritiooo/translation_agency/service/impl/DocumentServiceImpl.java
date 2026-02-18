package com.qritiooo.translation_agency.service.impl;

import com.qritiooo.translation_agency.dto.request.DocumentRequest;
import com.qritiooo.translation_agency.dto.response.DocumentResponse;
import com.qritiooo.translation_agency.mapper.DocumentMapper;
import com.qritiooo.translation_agency.model.Document;
import com.qritiooo.translation_agency.model.Order;
import com.qritiooo.translation_agency.repository.DocumentRepository;
import com.qritiooo.translation_agency.repository.OrderRepository;
import com.qritiooo.translation_agency.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository docRepo;
    private final OrderRepository orderRepo;

    @Override
    public DocumentResponse create(DocumentRequest request) {
        Document d = new Document();
        DocumentMapper.updateEntity(d, request);

        if (request.getOrderId() != null) {
            Order o = orderRepo.findById(request.getOrderId()).orElseThrow();
            d.setOrder(o);
        }

        return DocumentMapper.toResponse(docRepo.save(d));
    }

    @Override
    public DocumentResponse update(Integer id, DocumentRequest request) {
        Document d = docRepo.findById(id).orElseThrow();
        DocumentMapper.updateEntity(d, request);

        if (request.getOrderId() != null) {
            Order o = orderRepo.findById(request.getOrderId()).orElseThrow();
            d.setOrder(o);
        }

        return DocumentMapper.toResponse(docRepo.save(d));
    }

    @Override
    public DocumentResponse getById(Integer id) {
        return DocumentMapper.toResponse(docRepo.findById(id).orElseThrow());
    }

    @Override
    public List<DocumentResponse> getAll(Integer orderId) {
        var list = (orderId != null) ? docRepo.findByOrder_Id(orderId) : docRepo.findAll();
        return list.stream().map(DocumentMapper::toResponse).toList();
    }

    @Override
    public void delete(Integer id) {
        docRepo.deleteById(id);
    }
}
