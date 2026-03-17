package com.qritiooo.translationagency.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Document response DTO")
public class DocumentResponse {
    private Integer id;
    private String type;
    private Integer pages;
    private Integer orderId;
}

