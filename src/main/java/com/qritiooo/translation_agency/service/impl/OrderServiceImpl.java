package com.qritiooo.translation_agency.service.impl;

import com.qritiooo.translation_agency.dto.OrderDto;
import com.qritiooo.translation_agency.model.Order;
import com.qritiooo.translation_agency.mapper.OrderMapper;
import com.qritiooo.translation_agency.repository.ClientRepository;
import com.qritiooo.translation_agency.repository.OrderRepository;
import com.qritiooo.translation_agency.repository.TranslatorRepository;
import com.qritiooo.translation_agency.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepo;
    private final ClientRepository clientRepo;
    private final TranslatorRepository translatorRepo;

    @Override
    public OrderDto create(OrderDto dto) {
        Order o = new Order();
        OrderMapper.updateEntity(o, dto);

        if (dto.getClientId() != null)
            o.setClient(clientRepo.findById(dto.getClientId()).orElseThrow());

        if (dto.getTranslatorId() != null)
            o.setTranslator(translatorRepo.findById(dto.getTranslatorId()).orElseThrow());

        return OrderMapper.toDto(orderRepo.save(o));
    }

    @Override
    public OrderDto update(Integer id, OrderDto dto) {
        Order o = orderRepo.findById(id).orElseThrow();
        OrderMapper.updateEntity(o, dto);

        if (dto.getClientId() != null)
            o.setClient(clientRepo.findById(dto.getClientId()).orElseThrow());

        if (dto.getTranslatorId() != null)
            o.setTranslator(translatorRepo.findById(dto.getTranslatorId()).orElseThrow());

        return OrderMapper.toDto(orderRepo.save(o));
    }

    @Override
    public OrderDto getById(Integer id) {
        return OrderMapper.toDto(orderRepo.findById(id).orElseThrow());
    }

    @Override
    public List<OrderDto> getAll(String status, Integer clientId, Integer translatorId) {
        List<Order> list;

        if (status != null) list = orderRepo.findByStatus(status);
        else if (clientId != null) list = orderRepo.findByClient_Id(clientId);
        else if (translatorId != null) list = orderRepo.findByTranslator_Id(translatorId);
        else list = orderRepo.findAll();

        return list.stream().map(OrderMapper::toDto).toList();
    }

    @Override
    public void delete(Integer id) {
        orderRepo.deleteById(id);
    }
}
