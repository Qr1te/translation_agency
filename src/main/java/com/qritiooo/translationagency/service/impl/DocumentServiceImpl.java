package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.cache.CacheKey;
import com.qritiooo.translationagency.cache.CacheManager;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository docRepo;
    private final OrderRepository orderRepo;
    private final CacheManager cacheManager;

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
        cacheManager.invalidate(Document.class, Order.class);
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
        cacheManager.invalidate(Document.class, Order.class);
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
        cacheManager.invalidate(Document.class, Order.class);
        return response;
    }

    @Override
    public DocumentResponse getById(Integer id) {
        return DocumentMapper.toResponse(docRepo.findById(id).orElseThrow());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DocumentResponse> getAll(Integer orderId, Pageable pageable) {
        Pageable sortedPageable = withDefaultSort(pageable);
        CacheKey key = new CacheKey(
                Document.class,
                "getAll",
                orderId,
                sortedPageable.getPageNumber(),
                sortedPageable.getPageSize(),
                sortedPageable.getSort().toString()
        );
        return cacheManager.computeIfAbsent(key, () -> {
            Page<Document> page = (orderId != null)
                    ? docRepo.findByOrder_Id(orderId, sortedPageable)
                    : docRepo.findAll(sortedPageable);
            return page.map(DocumentMapper::toResponse);
        });
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
        cacheManager.invalidate(Document.class, Order.class);
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

    private Pageable withDefaultSort(Pageable pageable) {
        if (pageable.getSort().isSorted()) {
            return pageable;
        }
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "id")
        );
    }

}

