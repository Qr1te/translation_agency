package com.qritiooo.translationagency.service;

import com.qritiooo.translationagency.dto.response.AsyncTaskCreatedResponse;
import com.qritiooo.translationagency.dto.response.OrderAsyncTaskStatsResponse;
import com.qritiooo.translationagency.dto.response.OrderTaskStatusResponse;
import com.qritiooo.translationagency.model.OrderStatus;

public interface OrderAsyncService {
    AsyncTaskCreatedResponse startOrderReportTask(
            OrderStatus status,
            Integer clientId,
            Integer translatorId,
            boolean demoFail
    );

    OrderTaskStatusResponse getTaskStatus(String taskId);

    OrderAsyncTaskStatsResponse getTaskStats();
}
