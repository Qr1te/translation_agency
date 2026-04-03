package com.qritiooo.translationagency.dto.response;

public record RaceConditionDemoResponse(
        String mode,
        int threadCount,
        int incrementsPerThread,
        int expectedValue,
        int actualValue,
        int lostUpdates,
        boolean raceConditionDetected
) {
}
