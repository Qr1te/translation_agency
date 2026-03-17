package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.config.CacheNames;
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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = CacheNames.DOCUMENTS_ALL)
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository docRepo;
    private final OrderRepository orderRepo;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.DOCUMENTS_ALL, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.ORDERS_ALL, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.ORDERS_BY_TITLE, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.ORDERS_SEARCH_JPQL, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.ORDERS_SEARCH_NATIVE, allEntries = true)
    })
    public DocumentResponse create(DocumentRequest request) {
        Document d = new Document();
        DocumentMapper.updateEntity(d, request);

        if (request.getOrderId() != null) {
            Order o = orderRepo.findById(request.getOrderId()).orElseThrow();
            bindDocumentToOrder(d, o);
        }

        return DocumentMapper.toResponse(docRepo.save(d));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.DOCUMENTS_ALL, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.ORDERS_ALL, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.ORDERS_BY_TITLE, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.ORDERS_SEARCH_JPQL, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.ORDERS_SEARCH_NATIVE, allEntries = true)
    })
    public DocumentResponse update(Integer id, DocumentRequest request) {
        Document d = docRepo.findById(id).orElseThrow();
        DocumentMapper.updateEntity(d, request);

        if (request.getOrderId() != null) {
            Order o = orderRepo.findById(request.getOrderId()).orElseThrow();
            bindDocumentToOrder(d, o);
        }

        return DocumentMapper.toResponse(docRepo.save(d));
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.DOCUMENTS_ALL, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.ORDERS_ALL, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.ORDERS_BY_TITLE, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.ORDERS_SEARCH_JPQL, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.ORDERS_SEARCH_NATIVE, allEntries = true)
    })
    public DocumentResponse patch(Integer id, DocumentRequest request) {
        Document d = docRepo.findById(id).orElseThrow();
        DocumentMapper.patchEntity(d, request);

        if (request.getOrderId() != null) {
            Order o = orderRepo.findById(request.getOrderId()).orElseThrow();
            bindDocumentToOrder(d, o);
        }

        return DocumentMapper.toResponse(docRepo.save(d));
    }

    @Override
    public DocumentResponse getById(Integer id) {
        return DocumentMapper.toResponse(docRepo.findById(id).orElseThrow());
    }

    @Override
    @Cacheable(sync = true)
    public List<DocumentResponse> getAll(Integer orderId) {
        var list = (orderId != null)
                ? docRepo.findByOrder_Id(orderId)
                : docRepo.findAll();
        return list.stream().map(DocumentMapper::toResponse).toList();
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = CacheNames.DOCUMENTS_ALL, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.ORDERS_ALL, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.ORDERS_BY_TITLE, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.ORDERS_SEARCH_JPQL, allEntries = true),
            @CacheEvict(cacheNames = CacheNames.ORDERS_SEARCH_NATIVE, allEntries = true)
    })
    public void delete(Integer id) {
        Document document = docRepo.findById(id).orElseThrow();
        Order order = document.getOrder();

        if (order != null) {
            order.getDocuments().remove(document);
            document.setOrder(null);
        }

        docRepo.delete(document);
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

}

