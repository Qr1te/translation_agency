package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.dto.request.OrderRequest;
import com.qritiooo.translationagency.dto.response.OrderResponse;
import com.qritiooo.translationagency.mapper.OrderMapper;
import com.qritiooo.translationagency.model.Document;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final ClientRepository clientRepo;
    private final TranslatorRepository translatorRepo;
    private final DocumentRepository documentRepo;

    @Override
    @Transactional
    public OrderResponse create(OrderRequest request) {
        Order o = new Order();
        OrderMapper.updateEntity(o, request);
        applyRelations(o, request);
        return saveWithDocumentsAndMap(o, request.getDocumentIds());
    }

    @Override
    @Transactional
    public OrderResponse update(Integer id, OrderRequest request) {
        Order o = orderRepo.findById(id).orElseThrow();
        OrderMapper.updateEntity(o, request);
        applyRelations(o, request);
        return saveWithDocumentsAndMap(o, request.getDocumentIds());
    }

    @Override
    public OrderResponse getById(Integer id) {
        return OrderMapper.toResponse(orderRepo.findById(id).orElseThrow());
    }

    @Override
    public OrderResponse getByTitle(String title) {
        List<Order> orders = orderRepo.findByTitle(title);
        if (orders.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Order not found with title: " + title
            );
        }
        return OrderMapper.toResponse(orders.getFirst());
    }

    @Override
    public List<OrderResponse> getAll(String status, Integer clientId, Integer translatorId) {
        return findByFilters(status, clientId, translatorId).stream()
                .sorted(Comparator.comparing(Order::getId))
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> searchByNestedJpql(
            String status,
            String languageCode,
            Pageable pageable
    ) {
        return orderRepo.searchByNestedJpql(status, languageCode, pageable)
                .map(OrderMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> searchByNestedNative(
            String status,
            String languageCode,
            Pageable pageable
    ) {
        return orderRepo.searchByNestedNative(status, languageCode, pageable)
                .map(OrderMapper::toResponse);
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
        savedOrder.setDocuments(documentRepo.findByOrder_Id(savedOrder.getId()));
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
}

