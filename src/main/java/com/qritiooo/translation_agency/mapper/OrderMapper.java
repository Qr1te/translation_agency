package com.qritiooo.translation_agency.mapper;

import com.qritiooo.translation_agency.dto.OrderDto;
import com.qritiooo.translation_agency.model.Document;
import com.qritiooo.translation_agency.model.Order;

public class OrderMapper {
    public static OrderDto toDto(Order o) {
        Integer clientId = o.getClient() != null ? o.getClient().getId() : null;
        Integer translatorId = o.getTranslator() != null ? o.getTranslator().getId() : null;
        var docIds = o.getDocuments().stream().map(Document::getId).toList();

        return new OrderDto(o.getId(), o.getTitle(), o.getStatus(), clientId, translatorId, docIds);
    }

    public static void updateEntity(Order o, OrderDto dto) {
        o.setTitle(dto.getTitle());
        o.setStatus(dto.getStatus());
    }
}

