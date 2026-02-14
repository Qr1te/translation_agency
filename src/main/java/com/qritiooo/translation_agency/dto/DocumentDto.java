package com.qritiooo.translation_agency.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto {
    private Integer id;
    private String type;
    private Integer pages;
    private Integer orderId;
}
