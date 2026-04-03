package com.qritiooo.translationagency.service.impl;

import com.qritiooo.translationagency.dto.response.OrderReportResponse;
import com.qritiooo.translationagency.model.Order;
import com.qritiooo.translationagency.model.OrderStatus;
import com.qritiooo.translationagency.repository.OrderRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderReportAsyncProcessor {

    private final OrderRepository orderRepository;
    private final OrderAsyncTaskRegistry taskRegistry;

    @Async
    public CompletableFuture<Void> generateReport(
            String taskId,
            OrderStatus status,
            Integer clientId,
            Integer translatorId
    ) {
        taskRegistry.markRunning(taskId);
        try {
            List<Order> filteredOrders = orderRepository.findAll()
                    .stream()
                    .filter(order -> status == null || order.getStatus() == status)
                    .filter(order -> clientId == null || hasClientId(order, clientId))
                    .filter(order -> translatorId == null || hasTranslatorId(order, translatorId))
                    .toList();

            OrderReportResponse report = buildReport(filteredOrders);
            taskRegistry.markCompleted(taskId, report);
            return CompletableFuture.completedFuture(null);
        } catch (RuntimeException ex) {
            taskRegistry.markFailed(taskId, ex.getMessage());
            throw ex;
        }
    }

    private OrderReportResponse buildReport(List<Order> orders) {
        Map<String, Long> statusBreakdown = Arrays.stream(OrderStatus.values())
                .collect(java.util.stream.Collectors.toMap(
                        Enum::name,
                        currentStatus -> orders.stream()
                                .filter(order -> order.getStatus() == currentStatus)
                                .count()
                ));

        long ordersWithTranslator = orders.stream()
                .filter(order -> order.getTranslator() != null)
                .count();
        long totalAttachedDocuments = orders.stream()
                .map(Order::getDocuments)
                .filter(documents -> documents != null)
                .mapToLong(List::size)
                .sum();

        return new OrderReportResponse(
                orders.size(),
                statusBreakdown,
                ordersWithTranslator,
                orders.size() - ordersWithTranslator,
                totalAttachedDocuments
        );
    }

    private boolean hasClientId(Order order, Integer clientId) {
        return order.getClient() != null && clientId.equals(order.getClient().getId());
    }

    private boolean hasTranslatorId(Order order, Integer translatorId) {
        return order.getTranslator() != null
                && translatorId.equals(order.getTranslator().getId());
    }
}
