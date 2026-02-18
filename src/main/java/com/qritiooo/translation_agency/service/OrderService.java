package com.qritiooo.translation_agency.service;

import com.qritiooo.translation_agency.dto.request.OrderRequest;
import com.qritiooo.translation_agency.dto.response.OrderResponse;

import java.util.List;


public interface OrderService {
    OrderResponse create(OrderRequest request);
    OrderResponse update(Integer id, OrderRequest request);
    OrderResponse getById(Integer id);
    OrderResponse getByTitle(String title);
    List<OrderResponse> getAll(String status, Integer clientId, Integer translatorId);
    void delete(Integer id);
}
