package com.qritiooo.translationagency.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatToolResponse {
    private Integer id;
    private String name;
    private String vendor;
    private String currentVersion;
    private Boolean cloudBased;
}
