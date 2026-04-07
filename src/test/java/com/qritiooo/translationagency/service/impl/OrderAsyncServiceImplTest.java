package com.qritiooo.translationagency.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qritiooo.translationagency.dto.response.OrderAsyncTaskStatsResponse;
import com.qritiooo.translationagency.dto.response.OrderTaskStatusResponse;
import com.qritiooo.translationagency.model.AsyncTaskStatus;
import com.qritiooo.translationagency.model.OrderStatus;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderAsyncServiceImplTest {

    @Mock
    private OrderAsyncTaskRegistry taskRegistry;

    @Mock
    private OrderReportAsyncProcessor orderReportAsyncProcessor;

    @InjectMocks
    private OrderAsyncServiceImpl orderAsyncService;

    @Test
    void startOrderReportTask_ShouldCreateTaskAndStartAsyncProcessor() {
        var response = orderAsyncService.startOrderReportTask(OrderStatus.NEW, 1, 2, false);

        assertNotNull(response.taskId());
        verify(taskRegistry).createTask(response.taskId());
        verify(orderReportAsyncProcessor).generateReport(
                response.taskId(),
                OrderStatus.NEW,
                1,
                2,
                false
        );
    }

    @Test
    void getTaskStatus_ShouldDelegateToRegistry() {
        OrderTaskStatusResponse response = new OrderTaskStatusResponse(
                "task-1",
                AsyncTaskStatus.COMPLETED,
                Instant.now(),
                Instant.now(),
                Instant.now(),
                null,
                null
        );
        when(taskRegistry.getTaskStatus("task-1")).thenReturn(response);

        OrderTaskStatusResponse actual = orderAsyncService.getTaskStatus("task-1");

        assertEquals(AsyncTaskStatus.COMPLETED, actual.status());
        verify(taskRegistry).getTaskStatus("task-1");
    }

    @Test
    void getTaskStats_ShouldDelegateToRegistry() {
        OrderAsyncTaskStatsResponse response = new OrderAsyncTaskStatsResponse(3, 1, 1, 1);
        when(taskRegistry.getTaskStats()).thenReturn(response);

        OrderAsyncTaskStatsResponse actual = orderAsyncService.getTaskStats();

        assertEquals(3, actual.totalCreated());
        verify(taskRegistry).getTaskStats();
    }
}
