package com.qritiooo.translationagency.api.error;

public record ApiValidationError(
        String field,
        String message
) {
}
