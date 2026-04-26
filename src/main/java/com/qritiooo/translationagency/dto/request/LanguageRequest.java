package com.qritiooo.translationagency.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Language create/update request")
public class LanguageRequest {

    @NotBlank(message = "code is required")
    @Size(max = 16, message = "code must be at most 16 characters")
    @Schema(description = "Language code", example = "EN")
    private String code;

    @NotBlank(message = "name is required")
    @Size(max = 255, message = "name must be at most 255 characters")
    @Schema(description = "Language name", example = "English")
    private String name;
}
