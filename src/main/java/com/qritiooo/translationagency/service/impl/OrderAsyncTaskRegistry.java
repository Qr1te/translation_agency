package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.dto.response.OrderAsyncTaskStatsResponse;
import com.qritiooo.translationagency.dto.response.OrderReportResponse;
import com.qritiooo.translationagency.dto.response.OrderTaskStatusResponse;
import com.qritiooo.translationagency.exception.NotFoundException;
import com.qritiooo.translationagency.model.AsyncTaskStatus;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class OrderAsyncTaskRegistry {

    private final ConcurrentHashMap<String, TaskState> tasks = new ConcurrentHashMap<>();
    private final AtomicInteger totalCreated = new AtomicInteger();
    private final AtomicInteger currentlyRunning = new AtomicInteger();
    private final AtomicInteger completed = new AtomicInteger();
    private final AtomicInteger failed = new AtomicInteger();

    public void createTask(String taskId) {
        tasks.put(taskId, new TaskState(taskId));
        totalCreated.incrementAndGet();
    }

    public void markRunning(String taskId) {
        getTaskState(taskId).markRunning();
        currentlyRunning.incrementAndGet();
    }

    public void markCompleted(String taskId, OrderReportResponse result) {
        getTaskState(taskId).markCompleted(result);
        currentlyRunning.decrementAndGet();
        completed.incrementAndGet();
    }

    public void markFailed(String taskId, String errorMessage) {
        getTaskState(taskId).markFailed(errorMessage);
        currentlyRunning.decrementAndGet();
        failed.incrementAndGet();
    }

    public OrderTaskStatusResponse getTaskStatus(String taskId) {
        return getTaskState(taskId).toResponse();
    }

    public OrderAsyncTaskStatsResponse getTaskStats() {
        return new OrderAsyncTaskStatsResponse(
                totalCreated.get(),
                currentlyRunning.get(),
                completed.get(),
                failed.get()
        );
    }

    private TaskState getTaskState(String taskId) {
        TaskState state = tasks.get(taskId);
        if (state == null) {
            throw new NotFoundException("Async task not found: " + taskId);
        }
        return state;
    }

    private static final class TaskState {
        private final String taskId;
        private final Instant createdAt;
        private AsyncTaskStatus status;
        private Instant startedAt;
        private Instant completedAt;
        private String errorMessage;
        private OrderReportResponse result;

        private TaskState(String taskId) {
            this.taskId = taskId;
            this.createdAt = Instant.now();
            this.status = AsyncTaskStatus.PENDING;
        }

        private synchronized void markRunning() {
            status = AsyncTaskStatus.RUNNING;
            startedAt = Instant.now();
        }

        private synchronized void markCompleted(OrderReportResponse completedResult) {
            status = AsyncTaskStatus.COMPLETED;
            completedAt = Instant.now();
            result = completedResult;
            errorMessage = null;
        }

        private synchronized void markFailed(String taskErrorMessage) {
            status = AsyncTaskStatus.FAILED;
            completedAt = Instant.now();
            errorMessage = taskErrorMessage;
            result = null;
        }

        private synchronized OrderTaskStatusResponse toResponse() {
            return new OrderTaskStatusResponse(
                    taskId,
                    status,
                    createdAt,
                    startedAt,
                    completedAt,
                    errorMessage,
                    result
            );
        }
    }
}