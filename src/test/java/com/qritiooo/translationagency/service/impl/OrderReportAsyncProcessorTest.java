package com.qritiooo.translationagency.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.qritiooo.translationagency.dto.response.OrderReportResponse;
import com.qritiooo.translationagency.model.Client;
import com.qritiooo.translationagency.model.Document;
import com.qritiooo.translationagency.model.Language;
import com.qritiooo.translationagency.model.Order;
import com.qritiooo.translationagency.model.OrderStatus;
import com.qritiooo.translationagency.model.Translator;
import com.qritiooo.translationagency.repository.OrderRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderReportAsyncProcessorTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderAsyncTaskRegistry taskRegistry;

    @InjectMocks
    private OrderReportAsyncProcessor processor;

    @Test
    void generateReport_ShouldBuildReportAndMarkCompleted() {
        when(orderRepository.findAll()).thenReturn(List.of(
                buildOrder(1, OrderStatus.NEW, 10, 100, 2),
                buildOrder(2, OrderStatus.NEW, 10, null, 0),
                buildOrder(3, OrderStatus.COMPLETED, 20, 200, 1)
        ));

        processor.generateReport("task-1", OrderStatus.NEW, 10, null, false).join();

        ArgumentCaptor<OrderReportResponse> captor =
                ArgumentCaptor.forClass(OrderReportResponse.class);
        verify(taskRegistry).markRunning("task-1");
        verify(taskRegistry).markCompleted(eq("task-1"), captor.capture());
        OrderReportResponse result = captor.getValue();
        assertEquals(2, result.totalOrders());
        assertEquals(2L, result.statusBreakdown().get("NEW"));
        assertEquals(1, result.ordersWithTranslator());
        assertEquals(1, result.ordersWithoutTranslator());
        assertEquals(2, result.totalAttachedDocuments());
    }

    @Test
    void generateReport_ShouldFilterByClientAndTranslator() {
        when(orderRepository.findAll()).thenReturn(List.of(
                buildOrder(1, OrderStatus.NEW, 10, 100, 1),
                buildOrder(2, OrderStatus.NEW, null, 100, 1),
                buildOrder(3, OrderStatus.NEW, 10, null, -1),
                buildOrder(4, OrderStatus.NEW, 10, 200, 1)
        ));

        processor.generateReport("task-filter", null, 10, 100, false).join();

        ArgumentCaptor<OrderReportResponse> captor =
                ArgumentCaptor.forClass(OrderReportResponse.class);
        verify(taskRegistry).markCompleted(eq("task-filter"), captor.capture());
        OrderReportResponse result = captor.getValue();
        assertEquals(1, result.totalOrders());
        assertEquals(1L, result.statusBreakdown().get("NEW"));
        assertEquals(1, result.ordersWithTranslator());
        assertEquals(0, result.ordersWithoutTranslator());
        assertEquals(1, result.totalAttachedDocuments());
    }

    @Test
    void generateReport_ShouldFilterByClientOnly() {
        when(orderRepository.findAll()).thenReturn(List.of(
                buildOrder(1, OrderStatus.NEW, 10, 100, 1),
                buildOrder(2, OrderStatus.NEW, null, 100, 1),
                buildOrder(3, OrderStatus.NEW, 20, 100, 1)
        ));

        processor.generateReport("task-client", null, 10, null, false).join();

        ArgumentCaptor<OrderReportResponse> captor =
                ArgumentCaptor.forClass(OrderReportResponse.class);
        verify(taskRegistry).markCompleted(eq("task-client"), captor.capture());
        OrderReportResponse result = captor.getValue();
        assertEquals(1, result.totalOrders());
        assertEquals(1L, result.statusBreakdown().get("NEW"));
    }

    @Test
    void generateReport_ShouldIgnoreClientFilterWhenClientIdIsNull() {
        when(orderRepository.findAll()).thenReturn(List.of(
                buildOrder(1, OrderStatus.NEW, 10, 100, 1),
                buildOrder(2, OrderStatus.NEW, null, 100, 1),
                buildOrder(3, OrderStatus.NEW, 20, 100, 1)
        ));

        processor.generateReport("task-no-client-filter", null, null, 100, false).join();

        ArgumentCaptor<OrderReportResponse> captor =
                ArgumentCaptor.forClass(OrderReportResponse.class);
        verify(taskRegistry).markCompleted(eq("task-no-client-filter"), captor.capture());
        OrderReportResponse result = captor.getValue();
        assertEquals(3, result.totalOrders());
        assertEquals(3L, result.statusBreakdown().get("NEW"));
    }

    @Test
    void generateReport_ShouldMarkFailedWhenDemoFailEnabled() {
        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> processor.generateReport("task-demo-fail", null, null, null, true).join()
        );

        assertTrue(ex.getMessage().contains("Demo failure"));
        verify(taskRegistry).markRunning("task-demo-fail");
        verify(taskRegistry).markFailed("task-demo-fail", "Demo failure");
    }

    @Test
    void generateReport_ShouldMarkFailedAndRethrowWhenRepositoryFails() {
        when(orderRepository.findAll()).thenThrow(new IllegalStateException("DB error"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> joinReport("task-2")
        );

        assertTrue(ex.getMessage().contains("DB error"));
        verify(taskRegistry).markRunning("task-2");
        verify(taskRegistry).markFailed("task-2", "DB error");
    }

    private Order buildOrder(
            int id,
            OrderStatus status,
            Integer clientId,
            Integer translatorId,
            int documentsCount
    ) {
        Order order = new Order();
        order.setId(id);
        order.setTitle("Order " + id);
        order.setStatus(status);

        if (clientId != null) {
            Client client = new Client();
            client.setId(clientId);
            order.setClient(client);
        }

        if (translatorId != null) {
            Translator translator = new Translator();
            translator.setId(translatorId);
            order.setTranslator(translator);
        }

        if (documentsCount >= 0) {
            List<Document> documents = java.util.stream.IntStream.range(0, documentsCount)
                    .mapToObj(index -> new Document(index + 1, "Doc" + index, 1, order))
                    .toList();
            order.setDocuments(documents);
        } else {
            order.setDocuments(null);
        }
        order.setSourceLanguage(new Language(1, "EN", "English"));
        order.setTargetLanguage(new Language(2, "RU", "Russian"));
        return order;
    }

    private void joinReport(String taskId) {
        processor.generateReport(taskId, null, null, null, false).join();
    }
}