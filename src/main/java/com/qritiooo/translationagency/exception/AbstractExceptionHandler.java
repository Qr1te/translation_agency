package com.qritiooo.translationagency.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;

public abstract class AbstractExceptionHandler {

    protected static final String VALIDATION_FAILED_MESSAGE = "Validation failed";

    protected ResponseEntity<ApiErrorResponse> buildError(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            List<ApiValidationError> validationErrors
    ) {
        return ResponseEntity.status(status).body(
                new ApiErrorResponse(
                        Instant.now(),
                        status.value(),
                        status.getReasonPhrase(),
                        message,
                        request.getRequestURI(),
                        validationErrors
                )
        );
    }

    protected ApiValidationError toValidationError(FieldError error) {
        return new ApiValidationError(error.getField(), error.getDefaultMessage());
    }
}
