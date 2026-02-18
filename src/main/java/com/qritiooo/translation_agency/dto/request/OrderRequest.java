package com.qritiooo.translation_agency.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private String title;
    private String status;
    private Integer clientId;
    private Integer translatorId;
    private List<Integer> documentIds;
}
