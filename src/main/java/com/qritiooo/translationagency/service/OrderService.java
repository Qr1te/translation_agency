package com.qritiooo.translationagency.service;

import com.qritiooo.translationagency.dto.request.OrderRequest;
import com.qritiooo.translationagency.dto.response.OrderResponse;
import com.qritiooo.translationagency.model.OrderStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse create(OrderRequest request);

    OrderResponse update(Integer id, OrderRequest request);

    OrderResponse patch(Integer id, OrderRequest request);

    OrderResponse getById(Integer id);

    OrderResponse getByTitle(String title);

    List<OrderResponse> getAll(OrderStatus status, Integer clientId, Integer translatorId);

    Page<OrderResponse> searchByNestedJpql(
            OrderStatus status,
            String languageCode,
            Pageable pageable
    );

    Page<OrderResponse> searchByNestedNative(
            OrderStatus status,
            String languageCode,
            Pageable pageable
    );

    void delete(Integer id);
}

