package com.qritiooo.translationagency.dto.response;

import java.util.Map;

public record OrderReportResponse(
        long totalOrders,
        Map<String, Long> statusBreakdown,
        long ordersWithTranslator,
        long ordersWithoutTranslator,
        long totalAttachedDocuments
) {
}
