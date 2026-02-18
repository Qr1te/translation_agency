package com.qritiooo.translation_agency.mapper;

import com.qritiooo.translation_agency.dto.request.DocumentRequest;
import com.qritiooo.translation_agency.dto.response.DocumentResponse;
import com.qritiooo.translation_agency.model.Document;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DocumentMapper {

    public static DocumentResponse toResponse(Document d) {
        Integer orderId = d.getOrder() != null ? d.getOrder().getId() : null;
        return new DocumentResponse(d.getId(), d.getType(), d.getPages(), orderId);
    }

    public static void updateEntity(Document d, DocumentRequest request) {
        d.setType(request.getType());
        d.setPages(request.getPages());
    }
}
