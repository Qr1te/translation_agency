package com.qritiooo.translationagency.dto.request;

import com.qritiooo.translationagency.api.validation.OnCreate;
import com.qritiooo.translationagency.api.validation.OnUpdate;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Document create/update request")
public class DocumentRequest {
    @NotBlank(groups = {OnCreate.class, OnUpdate.class}, message = "type is required")
    @Size(max = 100, message = "type must be at most 100 characters")
    @Schema(description = "Document type", example = "Passport")
    private String type;

    @NotNull(groups = {OnCreate.class, OnUpdate.class}, message = "pages is required")
    @Positive(message = "pages must be greater than zero")
    @Schema(description = "Number of pages", example = "12")
    private Integer pages;

    @Positive(message = "orderId must be greater than zero")
    @Schema(description = "Related order id", example = "1")
    private Integer orderId;
}

