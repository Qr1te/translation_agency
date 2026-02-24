package com.qritiooo.translationagency.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

