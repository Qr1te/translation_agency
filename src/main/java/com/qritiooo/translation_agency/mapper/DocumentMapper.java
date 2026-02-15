package com.qritiooo.translation_agency.mapper;

import com.qritiooo.translation_agency.dto.DocumentDto;
import com.qritiooo.translation_agency.model.Document;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DocumentMapper {

    public static DocumentDto toDto(Document d) {
        Integer orderId = d.getOrder() != null ? d.getOrder().getId() : null;
        return new DocumentDto(d.getId(), d.getType(), d.getPages(), orderId);
    }

    public static void updateEntity(Document d, DocumentDto dto) {
        d.setType(dto.getType());
        d.setPages(dto.getPages());
    }
}
