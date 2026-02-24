package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.dto.request.DocumentRequest;
import com.qritiooo.translationagency.dto.response.DocumentResponse;
import com.qritiooo.translationagency.mapper.DocumentMapper;
import com.qritiooo.translationagency.model.Document;
import com.qritiooo.translationagency.model.Order;
import com.qritiooo.translationagency.repository.DocumentRepository;
import com.qritiooo.translationagency.repository.OrderRepository;
import com.qritiooo.translationagency.service.DocumentService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

