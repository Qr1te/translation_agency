package com.qritiooo.translation_agency.service;

import com.qritiooo.translation_agency.dto.OrderDto;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    OrderDto create(OrderDto dto);
    OrderDto update(Integer id, OrderDto dto);
    OrderDto getById(Integer id);
    List<OrderDto> getAll(String status, Integer clientId, Integer translatorId);
    void delete(Integer id);
}
