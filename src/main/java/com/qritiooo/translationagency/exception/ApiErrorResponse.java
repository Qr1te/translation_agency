package com.qritiooo.translationagency.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.List;

@Schema(description = "Unified API error response")
public record ApiErrorResponse(
        @Schema(description = "Timestamp when the error happened")
        Instant timestamp,
        @Schema(description = "HTTP status code", example = "400")
        Integer status,
        @Schema(description = "HTTP status reason", example = "Bad Request")
        String error,
        @Schema(description = "Human-readable error message")
        String message,
        @Schema(description = "Request path", example = "/api/orders")
        String path,
        @Schema(description = "Validation error details")
        List<ApiValidationError> validationErrors
) {
}
