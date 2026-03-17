package com.qritiooo.translationagency.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Document patch request")
public class DocumentPatchRequest {
    @Size(max = 100, message = "type must be at most 100 characters")
    @Schema(description = "Document type", example = "Passport")
    private String type;

    @Positive(message = "pages must be greater than zero")
    @Schema(description = "Number of pages", example = "12")
    private Integer pages;

    @Positive(message = "orderId must be greater than zero")
    @Schema(description = "Related order id", example = "1")
    private Integer orderId;
}
