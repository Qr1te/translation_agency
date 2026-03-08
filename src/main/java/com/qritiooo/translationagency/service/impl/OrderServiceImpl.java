package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.cache.CacheStore;
import com.qritiooo.translationagency.cache.CacheableService;
import com.qritiooo.translationagency.cache.HashMapCacheStore;
import com.qritiooo.translationagency.dto.request.OrderRequest;
import com.qritiooo.translationagency.dto.response.OrderResponse;
import com.qritiooo.translationagency.exception.BadRequestException;
import com.qritiooo.translationagency.exception.NotFoundException;
import com.qritiooo.translationagency.mapper.OrderMapper;
import com.qritiooo.translationagency.model.Document;
import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.model.Order;
import com.qritiooo.translationagency.repository.ClientRepository;
import com.qritiooo.translationagency.repository.DocumentRepository;
import com.qritiooo.translationagency.repository.OrderRepository;
import com.qritiooo.translationagency.repository.TranslatorRepository;
import com.qritiooo.translationagency.service.OrderService;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService, CacheableService {

    private final OrderRepository orderRepo;
    private final ClientRepository clientRepo;
    private final TranslatorRepository translatorRepo;
    private final DocumentRepository documentRepo;
    private final CacheStore cacheStore = new HashMapCacheStore();

    @Override
    @Transactional
    public OrderResponse create(OrderRequest request) {
        Order o = new Order();
        OrderMapper.updateEntity(o, request);
        applyRelations(o, request);
        OrderResponse response = saveWithDocumentsAndMap(o, request.getDocumentIds());
        invalidateCache();
        return response;
    }

    @Override
    @Transactional
    public OrderResponse update(Integer id, OrderRequest request) {
        Order o = orderRepo.findById(id).orElseThrow();
        OrderMapper.updateEntity(o, request);
        applyRelations(o, request);
        OrderResponse response = saveWithDocumentsAndMap(o, request.getDocumentIds());
        invalidateCache();
        return response;
    }

    @Override
    @Transactional
    public OrderResponse patch(Integer id, OrderRequest request) {
        Order o = orderRepo.findById(id).orElseThrow();
        OrderMapper.patchEntity(o, request);
        applyRelations(o, request);
        OrderResponse response = saveWithDocumentsAndMap(o, request.getDocumentIds());
        invalidateCache();
        return response;
    }

    @Override
    public OrderResponse getById(Integer id) {
        return OrderMapper.toResponse(orderRepo.findById(id).orElseThrow());
    }

    @Override
    public OrderResponse getByTitle(String title) {
        List<Order> orders = getOrLoad("getByTitle", () -> orderRepo.findByTitle(title), title);
        if (orders.isEmpty()) {
            throw new NotFoundException("Order not found with title: " + title);
        }
        return OrderMapper.toResponse(orders.getFirst());
    }

    @Override
    public List<OrderResponse> getAll(String status, Integer clientId, Integer translatorId) {
        return getOrLoad(
                "getAll",
                () -> findByFilters(status, clientId, translatorId).stream()
                        .sorted(Comparator.comparing(Order::getId))
                        .map(OrderMapper::toResponse)
                        .toList(),
                status,
                clientId,
                translatorId
        );
    }

    @Override
    public String getCacheNamespace() {
        return "order";
    }

    @Override
    public CacheStore getCacheStore() {
        return cacheStore;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> searchByNestedJpql(
            String status,
            String languageCode,
            Pageable pageable
    ) {
        Language language = parseLanguage(languageCode);
        return getOrLoad(
                "searchByNestedJpql",
                () -> orderRepo.searchByNestedJpql(status, language, pageable)
                        .map(OrderMapper::toResponse),
                status,
                language != null ? language.name() : null,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().toString()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> searchByNestedNative(
            String status,
            String languageCode,
            Pageable pageable
    ) {
        Language language = parseLanguage(languageCode);
        return getOrLoad(
                "searchByNestedNative",
                () -> orderRepo.searchByNestedNative(
                        status,
                        language != null ? language.name() : null,
                        pageable
                )
                        .map(OrderMapper::toResponse),
                status,
                language != null ? language.name() : null,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().toString()
        );
    }

    private void assignDocuments(Order order, List<Integer> documentIds) {
        if (documentIds == null) {
            return;
        }
        for (Integer documentId : documentIds) {
            Document document = documentRepo.findById(documentId).orElseThrow();
            document.setOrder(order);
            documentRepo.save(document);
        }
    }

    @Override
    public void delete(Integer id) {
        orderRepo.deleteById(id);
        invalidateCache();
    }

    private void applyRelations(Order order, OrderRequest request) {
        if (request.getClientId() != null) {
            order.setClient(clientRepo.findById(request.getClientId()).orElseThrow());
        }
        if (request.getTranslatorId() != null) {
            order.setTranslator(translatorRepo.findById(request.getTranslatorId()).orElseThrow());
        }
    }

    private OrderResponse saveWithDocumentsAndMap(Order order, List<Integer> documentIds) {
        Order savedOrder = orderRepo.save(order);
        assignDocuments(savedOrder, documentIds);
        List<Document> refreshedDocuments = documentRepo.findByOrder_Id(savedOrder.getId());
        savedOrder.getDocuments().clear();
        savedOrder.getDocuments().addAll(refreshedDocuments);
        return OrderMapper.toResponse(savedOrder);
    }

    private List<Order> findByFilters(String status, Integer clientId, Integer translatorId) {
        if (status != null) {
            return orderRepo.findByStatus(status);
        }
        if (clientId != null) {
            return orderRepo.findByClient_Id(clientId);
        }
        if (translatorId != null) {
            return orderRepo.findByTranslator_Id(translatorId);
        }
        return orderRepo.findAll();
    }

    private Language parseLanguage(String languageCode) {
        if (languageCode == null || languageCode.isBlank()) {
            return null;
        }
        try {
            return Language.fromCode(languageCode);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
}

