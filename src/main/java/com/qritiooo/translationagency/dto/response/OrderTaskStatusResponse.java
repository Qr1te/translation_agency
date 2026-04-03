package com.qritiooo.translationagency.dto.response;

import com.qritiooo.translationagency.model.AsyncTaskStatus;
import java.time.Instant;

public record OrderTaskStatusResponse(
        String taskId,
        AsyncTaskStatus status,
        Instant createdAt,
        Instant startedAt,
        Instant completedAt,
        String errorMessage,
        OrderReportResponse result
) {
}
