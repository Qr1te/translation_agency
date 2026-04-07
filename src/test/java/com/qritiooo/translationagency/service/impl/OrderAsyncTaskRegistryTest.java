package com.qritiooo.translationagency.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.qritiooo.translationagency.dto.response.OrderReportResponse;
import com.qritiooo.translationagency.exception.NotFoundException;
import com.qritiooo.translationagency.model.AsyncTaskStatus;
import java.util.Map;
import org.junit.jupiter.api.Test;

class OrderAsyncTaskRegistryTest {

    private final OrderAsyncTaskRegistry registry = new OrderAsyncTaskRegistry();

    @Test
    void createAndCompleteTask_ShouldExposeStatusAndStats() {
        registry.createTask("task-1");
        registry.markRunning("task-1");
        registry.markCompleted(
                "task-1",
                new OrderReportResponse(2, Map.of("NEW", 2L), 1, 1, 4)
        );

        var status = registry.getTaskStatus("task-1");
        var stats = registry.getTaskStats();

        assertEquals(AsyncTaskStatus.COMPLETED, status.status());
        assertEquals(2, status.result().totalOrders());
        assertEquals(1, stats.totalCreated());
        assertEquals(0, stats.currentlyRunning());
        assertEquals(1, stats.completed());
        assertEquals(0, stats.failed());
    }

    @Test
    void markFailed_ShouldExposeFailureStatusAndStats() {
        registry.createTask("task-2");
        registry.markRunning("task-2");
        registry.markFailed("task-2", "boom");

        var status = registry.getTaskStatus("task-2");
        var stats = registry.getTaskStats();

        assertEquals(AsyncTaskStatus.FAILED, status.status());
        assertEquals("boom", status.errorMessage());
        assertEquals(1, stats.failed());
    }

    @Test
    void getTaskStatus_ShouldThrowWhenTaskMissing() {
        NotFoundException ex = assertThrows(
                NotFoundException.class,
                () -> registry.getTaskStatus("missing")
        );

        assertTrue(ex.getMessage().contains("missing"));
    }
}