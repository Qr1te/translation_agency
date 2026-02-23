package com.qritiooo.translation_agency.service.impl;

import com.qritiooo.translation_agency.dto.request.OrderRequest;
import com.qritiooo.translation_agency.dto.response.OrderResponse;
import com.qritiooo.translation_agency.mapper.OrderMapper;
import com.qritiooo.translation_agency.model.Document;
import com.qritiooo.translation_agency.model.Order;
import com.qritiooo.translation_agency.repository.ClientRepository;
import com.qritiooo.translation_agency.repository.DocumentRepository;
import com.qritiooo.translation_agency.repository.OrderRepository;
import com.qritiooo.translation_agency.repository.TranslatorRepository;
import com.qritiooo.translation_agency.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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

        if (request.getClientId() != null)
            o.setClient(clientRepo.findById(request.getClientId()).orElseThrow());

        if (request.getTranslatorId() != null)
            o.setTranslator(translatorRepo.findById(request.getTranslatorId()).orElseThrow());

        Order savedOrder = orderRepo.save(o);
        assignDocuments(savedOrder, request.getDocumentIds());
        return OrderMapper.toResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse update(Integer id, OrderRequest request) {
        Order o = orderRepo.findById(id).orElseThrow();
        OrderMapper.updateEntity(o, request);

        if (request.getClientId() != null)
            o.setClient(clientRepo.findById(request.getClientId()).orElseThrow());

        if (request.getTranslatorId() != null)
            o.setTranslator(translatorRepo.findById(request.getTranslatorId()).orElseThrow());

        Order savedOrder = orderRepo.save(o);
        assignDocuments(savedOrder, request.getDocumentIds());
        return OrderMapper.toResponse(savedOrder);
    }

    @Override
    public OrderResponse getById(Integer id) {
        return OrderMapper.toResponse(orderRepo.findById(id).orElseThrow());
    }

    @Override
    public OrderResponse getByTitle(String title) {
        List<Order> orders = orderRepo.findByTitle(title);
        if (orders.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found with title: " + title);
        }
        return OrderMapper.toResponse(orders.getFirst());
    }
    @Override
    public List<OrderResponse> getAll(String status, Integer clientId, Integer translatorId) {
        List<Order> list;

        if (status != null) list = orderRepo.findByStatus(status);
        else if (clientId != null) list = orderRepo.findByClient_Id(clientId);
        else if (translatorId != null) list = orderRepo.findByTranslator_Id(translatorId);
        else list = orderRepo.findAll();

        return list.stream().map(OrderMapper::toResponse).toList();
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
}
