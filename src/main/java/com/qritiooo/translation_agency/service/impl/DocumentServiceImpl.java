package com.qritiooo.translation_agency.service.impl;

import com.qritiooo.translation_agency.dto.DocumentDto;
import com.qritiooo.translation_agency.mapper.DocumentMapper;
import com.qritiooo.translation_agency.model.Document;
import com.qritiooo.translation_agency.model.Order;
import com.qritiooo.translation_agency.repository.DocumentRepository;
import com.qritiooo.translation_agency.repository.OrderRepository;
import com.qritiooo.translation_agency.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository docRepo;
    private final OrderRepository orderRepo;

    @Override
    public DocumentDto create(DocumentDto dto) {
        Document d = new Document();
        DocumentMapper.updateEntity(d, dto);

        if (dto.getOrderId() != null) {
            Order o = orderRepo.findById(dto.getOrderId()).orElseThrow();
            d.setOrder(o);
        }

        return DocumentMapper.toDto(docRepo.save(d));
    }

    @Override
    public DocumentDto update(Integer id, DocumentDto dto) {
        Document d = docRepo.findById(id).orElseThrow();
        DocumentMapper.updateEntity(d, dto);

        if (dto.getOrderId() != null) {
            Order o = orderRepo.findById(dto.getOrderId()).orElseThrow();
            d.setOrder(o);
        }

        return DocumentMapper.toDto(docRepo.save(d));
    }

    @Override
    public DocumentDto getById(Integer id) {
        return DocumentMapper.toDto(docRepo.findById(id).orElseThrow());
    }

    @Override
    public List<DocumentDto> getAll(Integer orderId) {
        var list = (orderId != null) ? docRepo.findByOrder_Id(orderId) : docRepo.findAll();
        return list.stream().map(DocumentMapper::toDto).toList();
    }

    @Override
    public void delete(Integer id) {
        docRepo.deleteById(id);
    }
}
