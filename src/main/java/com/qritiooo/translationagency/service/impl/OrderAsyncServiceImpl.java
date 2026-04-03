package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.dto.response.AsyncTaskCreatedResponse;
import com.qritiooo.translationagency.dto.response.OrderAsyncTaskStatsResponse;
import com.qritiooo.translationagency.dto.response.OrderTaskStatusResponse;
import com.qritiooo.translationagency.model.OrderStatus;
import com.qritiooo.translationagency.service.OrderAsyncService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderAsyncServiceImpl implements OrderAsyncService {

    private final OrderAsyncTaskRegistry taskRegistry;
    private final OrderReportAsyncProcessor orderReportAsyncProcessor;

    @Override
    public AsyncTaskCreatedResponse startOrderReportTask(
            OrderStatus status,
            Integer clientId,
            Integer translatorId
    ) {
        String taskId = UUID.randomUUID().toString();
        taskRegistry.createTask(taskId);
        orderReportAsyncProcessor.generateReport(taskId, status, clientId, translatorId);
        return new AsyncTaskCreatedResponse(taskId);
    }

    @Override
    public OrderTaskStatusResponse getTaskStatus(String taskId) {
        return taskRegistry.getTaskStatus(taskId);
    }

    @Override
    public OrderAsyncTaskStatsResponse getTaskStats() {
        return taskRegistry.getTaskStats();
    }
}
