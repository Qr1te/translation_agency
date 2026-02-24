package com.qritiooo.translationagency.service;

import com.qritiooo.translationagency.dto.request.OrderRequest;
import com.qritiooo.translationagency.dto.response.OrderResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderResponse create(OrderRequest request);

    OrderResponse update(Integer id, OrderRequest request);

    OrderResponse getById(Integer id);

    OrderResponse getByTitle(String title);

    List<OrderResponse> getAll(String status, Integer clientId, Integer translatorId);

    Page<OrderResponse> searchByNestedJpql(String status, String languageCode, Pageable pageable);

    Page<OrderResponse> searchByNestedNative(String status, String languageCode, Pageable pageable);

    void delete(Integer id);
}

