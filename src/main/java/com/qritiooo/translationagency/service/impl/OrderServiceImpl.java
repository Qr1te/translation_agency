package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.cache.CacheKey;
import com.qritiooo.translationagency.cache.CacheManager;
import com.qritiooo.translationagency.dto.request.OrderRequest;
import com.qritiooo.translationagency.dto.response.OrderResponse;
import com.qritiooo.translationagency.exception.NotFoundException;
import com.qritiooo.translationagency.mapper.OrderMapper;
import com.qritiooo.translationagency.model.Document;
import com.qritiooo.translationagency.model.Order;
import com.qritiooo.translationagency.model.OrderStatus;
import com.qritiooo.translationagency.repository.ClientRepository;
import com.qritiooo.translationagency.repository.DocumentRepository;
import com.qritiooo.translationagency.repository.LanguageRepository;
import com.qritiooo.translationagency.repository.OrderRepository;
import com.qritiooo.translationagency.repository.TranslatorRepository;
import com.qritiooo.translationagency.service.OrderService;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final ClientRepository clientRepo;
    private final TranslatorRepository translatorRepo;
    private final DocumentRepository documentRepo;
    private final LanguageRepository languageRepo;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    public OrderResponse create(OrderRequest request) {
        Order o = new Order();
        OrderMapper.updateEntity(o, request);

        Order savedOrder = orderRepo.save(o);
        if (request.getSourceLanguageId() != null) {
            savedOrder.setSourceLanguage(
                    languageRepo.findById(request.getSourceLanguageId()).orElseThrow()
            );
            savedOrder = orderRepo.save(savedOrder);
        }
        if (request.getTargetLanguageId() != null) {
            savedOrder.setTargetLanguage(
                    languageRepo.findById(request.getTargetLanguageId()).orElseThrow()
            );
            savedOrder = orderRepo.save(savedOrder);
        }
        if (request.getClientId() != null) {
            savedOrder.setClient(clientRepo.findById(request.getClientId()).orElseThrow());
            savedOrder = orderRepo.save(savedOrder);
        }
        if (request.getTranslatorId() != null) {
            savedOrder.setTranslator(
                    translatorRepo.findById(request.getTranslatorId()).orElseThrow()
            );
            savedOrder = orderRepo.save(savedOrder);
        }
        OrderResponse response = saveWithDocumentsAndMap(savedOrder, request.getDocumentIds());
        cacheManager.invalidate(Order.class);
        return response;
    }

    @Override
    @Transactional
    public OrderResponse update(Integer id, OrderRequest request) {
        Order o = orderRepo.findById(id).orElseThrow();
        OrderMapper.updateEntity(o, request);
        applyRelations(o, request);
        OrderResponse response = saveWithDocumentsAndMap(o, request.getDocumentIds());
        cacheManager.invalidate(Order.class);
        return response;
    }

    @Override
    @Transactional
    public OrderResponse patch(Integer id, OrderRequest request) {
        Order o = orderRepo.findById(id).orElseThrow();
        OrderMapper.patchEntity(o, request);
        applyRelations(o, request);
        OrderResponse response = saveWithDocumentsAndMap(o, request.getDocumentIds());
        cacheManager.invalidate(Order.class);
        return response;
    }

    @Override
    public OrderResponse getById(Integer id) {
        return OrderMapper.toResponse(orderRepo.findById(id).orElseThrow());
    }

    @Override
    public OrderResponse getByTitle(String title) {
        CacheKey key = new CacheKey(Order.class, "getByTitle", title);
        return cacheManager.computeIfAbsent(key, () -> {
            List<Order> orders = orderRepo.findByTitle(title);
            if (orders.isEmpty()) {
                throw new NotFoundException("Order not found with title: " + title);
            }
            return OrderMapper.toResponse(orders.getFirst());
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAll(
            OrderStatus status,
            Integer clientId,
            Integer translatorId,
            Pageable pageable
    ) {
        CacheKey key = new CacheKey(
                Order.class,
                "getAll",
                status,
                clientId,
                translatorId,
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().toString()
        );
        return cacheManager.computeIfAbsent(key, () ->
                orderRepo.findAllByFilters(status, clientId, translatorId, pageable)
                        .map(OrderMapper::toResponse)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findByStatusAndTranslatorLanguageJpql(
            OrderStatus status,
            String languageCode
    ) {
        CacheKey key = new CacheKey(
                Order.class,
                "findByStatusAndTranslatorLanguageJpql",
                status,
                languageCode
        );
        return cacheManager.computeIfAbsent(key, () -> {
            String normalizedLanguageCode = normalizeLanguageCodeOrNull(languageCode);
            List<Integer> orderIds = orderRepo.findIdsByStatusAndTranslatorLanguageJpql(
                    status,
                    normalizedLanguageCode
            );
            if (orderIds.isEmpty()) {
                return List.of();
            }
            return orderRepo.findAllWithDetailsByIdIn(orderIds)
                    .stream()
                    .map(OrderMapper::toResponse)
                    .toList();
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> findByStatusAndTranslatorLanguageNative(
            OrderStatus status,
            String languageCode
    ) {
        CacheKey key = new CacheKey(
                Order.class,
                "findByStatusAndTranslatorLanguageNative",
                status,
                languageCode
        );
        return cacheManager.computeIfAbsent(key, () -> {
            String normalizedLanguageCode = normalizeLanguageCodeOrNull(languageCode);
            List<Integer> orderIds = orderRepo.findIdsByStatusAndTranslatorLanguageNative(
                    status != null ? status.name() : null,
                    normalizedLanguageCode
            );
            if (orderIds.isEmpty()) {
                return List.of();
            }
            return orderRepo.findAllWithDetailsByIdIn(orderIds)
                    .stream()
                    .map(OrderMapper::toResponse)
                    .toList();
        });
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
        cacheManager.invalidate(Order.class);
    }

    private void applyRelations(Order order, OrderRequest request) {
        if (request.getClientId() != null) {
            order.setClient(clientRepo.findById(request.getClientId()).orElseThrow());
        }
        if (request.getTranslatorId() != null) {
            order.setTranslator(translatorRepo.findById(request.getTranslatorId()).orElseThrow());
        }
        if (request.getSourceLanguageId() != null) {
            order.setSourceLanguage(
                    languageRepo.findById(request.getSourceLanguageId()).orElseThrow()
            );
        }
        if (request.getTargetLanguageId() != null) {
            order.setTargetLanguage(
                    languageRepo.findById(request.getTargetLanguageId()).orElseThrow()
            );
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

    private String normalizeLanguageCodeOrNull(String languageCode) {
        if (languageCode == null || languageCode.isBlank()) {
            return null;
        }
        return normalizeLanguageCode(languageCode);
    }

    private String normalizeLanguageCode(String languageCode) {
        return switch (languageCode.trim().toUpperCase(Locale.ROOT)) {
            case "ENGLISH" -> "EN";
            case "RUSSIAN" -> "RU";
            case "GERMAN" -> "DE";
            case "FRENCH" -> "FR";
            case "ITALIAN" -> "IT";
            case "SPANISH" -> "SP";
            case "POLISH" -> "PL";
            case "CHINESE" -> "CN";
            default -> languageCode.trim().toUpperCase(Locale.ROOT);
        };
    }
}
