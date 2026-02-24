package com.qritiooo.translationagency.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequest {
    private String type;
    private Integer pages;
    private Integer orderId;
}

