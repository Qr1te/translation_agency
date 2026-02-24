package com.qritiooo.translationagency.mapper;

import com.qritiooo.translationagency.dto.request.OrderRequest;
import com.qritiooo.translationagency.dto.response.OrderResponse;
import com.qritiooo.translationagency.model.Document;
import com.qritiooo.translationagency.model.Order;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrderMapper {
    public static OrderResponse toResponse(Order o) {
        Integer clientId = o.getClient() != null ? o.getClient().getId() : null;
        Integer translatorId = o.getTranslator() != null ? o.getTranslator().getId() : null;
        var docIds = o.getDocuments().stream().map(Document::getId).toList();

        return new OrderResponse(
                o.getId(),
                o.getTitle(),
                o.getStatus(),
                clientId,
                translatorId,
                docIds
        );
    }

    public static void updateEntity(Order o, OrderRequest request) {
        o.setTitle(request.getTitle());
        o.setStatus(request.getStatus());
    }
}


