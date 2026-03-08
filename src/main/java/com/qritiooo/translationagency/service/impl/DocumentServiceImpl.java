package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.cache.CacheStore;
import com.qritiooo.translationagency.cache.CacheableService;
import com.qritiooo.translationagency.cache.HashMapCacheStore;
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
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService, CacheableService {

    private final DocumentRepository docRepo;
    private final OrderRepository orderRepo;
    private final OrderServiceImpl orderService;
    private final CacheStore cacheStore = new HashMapCacheStore();

    @Override
    @Transactional
    public DocumentResponse create(DocumentRequest request) {
        Document d = new Document();
        DocumentMapper.updateEntity(d, request);

        if (request.getOrderId() != null) {
            Order o = orderRepo.findById(request.getOrderId()).orElseThrow();
            bindDocumentToOrder(d, o);
        }

        DocumentResponse response = DocumentMapper.toResponse(docRepo.save(d));
        invalidateRelatedCaches();
        return response;
    }

    @Override
    @Transactional
    public DocumentResponse update(Integer id, DocumentRequest request) {
        Document d = docRepo.findById(id).orElseThrow();
        DocumentMapper.updateEntity(d, request);

        if (request.getOrderId() != null) {
            Order o = orderRepo.findById(request.getOrderId()).orElseThrow();
            bindDocumentToOrder(d, o);
        }

        DocumentResponse response = DocumentMapper.toResponse(docRepo.save(d));
        invalidateRelatedCaches();
        return response;
    }

    @Override
    @Transactional
    public DocumentResponse patch(Integer id, DocumentRequest request) {
        Document d = docRepo.findById(id).orElseThrow();
        DocumentMapper.patchEntity(d, request);

        if (request.getOrderId() != null) {
            Order o = orderRepo.findById(request.getOrderId()).orElseThrow();
            bindDocumentToOrder(d, o);
        }

        DocumentResponse response = DocumentMapper.toResponse(docRepo.save(d));
        invalidateRelatedCaches();
        return response;
    }

    @Override
    public DocumentResponse getById(Integer id) {
        return DocumentMapper.toResponse(docRepo.findById(id).orElseThrow());
    }

    @Override
    public List<DocumentResponse> getAll(Integer orderId) {
        return getOrLoad(
                "getAll",
                () -> {
                    var list = (orderId != null)
                            ? docRepo.findByOrder_Id(orderId)
                            : docRepo.findAll();
                    return list.stream().map(DocumentMapper::toResponse).toList();
                },
                orderId
        );
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        Document document = docRepo.findById(id).orElseThrow();
        Order order = document.getOrder();

        if (order != null) {
            order.getDocuments().remove(document);
            document.setOrder(null);
        }

        docRepo.delete(document);
        invalidateRelatedCaches();
    }

    @Override
    public String getCacheNamespace() {
        return "document";
    }

    @Override
    public CacheStore getCacheStore() {
        return cacheStore;
    }

    private void bindDocumentToOrder(Document document, Order newOrder) {
        Order oldOrder = document.getOrder();
        boolean orderChanged = oldOrder != null
                && oldOrder.getId() != null
                && !oldOrder.getId().equals(newOrder.getId());
        if (orderChanged) {
            oldOrder.getDocuments().remove(document);
        }
        document.setOrder(newOrder);
        if (!newOrder.getDocuments().contains(document)) {
            newOrder.getDocuments().add(document);
        }
    }

    private void invalidateRelatedCaches() {
        invalidateCache();
        orderService.invalidateCache();
    }
}

