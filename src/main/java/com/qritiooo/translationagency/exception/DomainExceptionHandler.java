package com.qritiooo.translationagency.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(3)
public class DomainExceptionHandler extends AbstractExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            NotFoundException ex,
            HttpServletRequest request
    ) {
        logHandledException(HttpStatus.NOT_FOUND, ex.getMessage(), request);
        return buildError(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request,
                List.of()
        );
    }

    @ExceptionHandler({BadRequestException.class, IllegalArgumentException.class})
    public ResponseEntity<ApiErrorResponse> handleBadRequest(
            RuntimeException ex,
            HttpServletRequest request
    ) {
        logHandledException(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
        return buildError(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request,
                List.of()
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            ConflictException ex,
            HttpServletRequest request
    ) {
        logHandledException(HttpStatus.CONFLICT, ex.getMessage(), request);
        return buildError(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                request,
                List.of()
        );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiErrorResponse> handleNoSuchElement(
            NoSuchElementException ex,
            HttpServletRequest request
    ) {
        logHandledException(HttpStatus.NOT_FOUND, "Resource not found", request);
        return buildError(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                request,
                List.of()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        String message = ex.getMostSpecificCause().getMessage();
        logHandledException(HttpStatus.CONFLICT, message, request);
        return buildError(
                HttpStatus.CONFLICT,
                message,
                request,
                List.of()
        );
    }
}

