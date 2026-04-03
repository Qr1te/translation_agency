package com.qritiooo.translationagency.dto.response;

public record OrderAsyncTaskStatsResponse(
        int totalCreated,
        int currentlyRunning,
        int completed,
        int failed
) {
}
