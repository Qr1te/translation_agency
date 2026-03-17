package com.qritiooo.translationagency.dto.request;

import com.qritiooo.translationagency.model.OrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Order request")
public class OrderRequest {
    @NotBlank(message = "title is required")
    @Size(max = 255, message = "title must be at most 255 characters")
    @Schema(description = "Order title", example = "Translate contract")
    private String title;

    @NotNull(message = "status is required")
    @Schema(description = "Current order status")
    private OrderStatus status;

    @Positive(message = "sourceLanguageId must be greater than zero")
    @Schema(description = "Source language id", example = "1")
    private Integer sourceLanguageId;

    @Positive(message = "targetLanguageId must be greater than zero")
    @Schema(description = "Target language id", example = "2")
    private Integer targetLanguageId;

    @Positive(message = "clientId must be greater than zero")
    @Schema(description = "Client id", example = "1")
    private Integer clientId;

    @Positive(message = "translatorId must be greater than zero")
    @Schema(description = "Translator id", example = "3")
    private Integer translatorId;

    @Schema(description = "Attached document ids")
    private List<@Positive(message = "documentId must be greater than zero") Integer> documentIds;
}

