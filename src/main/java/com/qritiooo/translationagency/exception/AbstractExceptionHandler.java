package com.qritiooo.translationagency.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;

public abstract class AbstractExceptionHandler {

    protected static final String VALIDATION_FAILED_MESSAGE = "Validation failed";
    private static final Logger log = LoggerFactory.getLogger(AbstractExceptionHandler.class);

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

    protected void logHandledException(
            HttpStatus status,
            String message,
            HttpServletRequest request,
            Exception ex
    ) {
        if (status.is4xxClientError()) {
            log.warn(
                    "Handled HTTP {} {} error for {} {}: {}",
                    status.value(),
                    status.getReasonPhrase(),
                    request.getMethod(),
                    request.getRequestURI(),
                    message,
                    ex
            );
        }
    }

    protected ApiValidationError toValidationError(FieldError error) {
        return new ApiValidationError(error.getField(), error.getDefaultMessage());
    }
}
