package com.qritiooo.translation_agency.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentResponse {
    private Integer id;
    private String type;
    private Integer pages;
    private Integer orderId;
}
