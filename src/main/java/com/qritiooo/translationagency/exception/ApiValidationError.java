package com.qritiooo.translationagency.exception;

public record ApiValidationError(
        String field,
        String message
) {
}
