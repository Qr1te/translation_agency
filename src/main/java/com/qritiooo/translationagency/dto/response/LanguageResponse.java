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
@Schema(description = "Language response DTO")
public class LanguageResponse {
    private Integer id;
    private String code;
    private String name;
}

