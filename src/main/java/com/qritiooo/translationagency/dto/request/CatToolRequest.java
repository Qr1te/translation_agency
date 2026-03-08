package com.qritiooo.translationagency.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CatToolRequest {
    private String name;
    private String vendor;
    private String currentVersion;
    private Boolean cloudBased;
}
