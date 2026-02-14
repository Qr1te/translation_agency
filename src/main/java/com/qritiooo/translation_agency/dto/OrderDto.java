package com.qritiooo.translation_agency.dto;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class OrderDto {
    private Integer id;
    private String title;
    private String status;

    private Integer clientId;
    private Integer translatorId;
    private List<Integer> documentIds;
}
