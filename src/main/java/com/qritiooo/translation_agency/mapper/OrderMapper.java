package com.qritiooo.translation_agency.mapper;

import com.qritiooo.translation_agency.dto.request.OrderRequest;
import com.qritiooo.translation_agency.dto.response.OrderResponse;
import com.qritiooo.translation_agency.model.Document;
import com.qritiooo.translation_agency.model.Order;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderMapper {
    public static OrderResponse toResponse(Order o) {
        Integer clientId = o.getClient() != null ? o.getClient().getId() : null;
        Integer translatorId = o.getTranslator() != null ? o.getTranslator().getId() : null;
        var docIds = o.getDocuments().stream().map(Document::getId).toList();

        return new OrderResponse(o.getId(), o.getTitle(), o.getStatus(), clientId, translatorId, docIds);
    }

    public static void updateEntity(Order o, OrderRequest request) {
        o.setTitle(request.getTitle());
        o.setStatus(request.getStatus());
    }
}

