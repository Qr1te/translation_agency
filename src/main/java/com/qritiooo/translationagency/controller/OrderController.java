package com.qritiooo.translationagency.controller;

import com.qritiooo.translationagency.dto.request.OrderRequest;
import com.qritiooo.translationagency.dto.response.AsyncTaskCreatedResponse;
import com.qritiooo.translationagency.dto.response.OrderAsyncTaskStatsResponse;
import com.qritiooo.translationagency.dto.response.OrderResponse;
import com.qritiooo.translationagency.dto.response.OrderTaskStatusResponse;
import com.qritiooo.translationagency.model.OrderStatus;
import com.qritiooo.translationagency.service.OrderAsyncService;
import com.qritiooo.translationagency.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
@Validated
@Tag(name = "Orders", description = "Order management and search endpoints")
public class OrderController {

    private final OrderService orderService;
    private final OrderAsyncService orderAsyncService;

    @PostMapping
    @Operation(summary = "Create order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<OrderResponse> create(
            @Valid @RequestBody OrderRequest request
    ) {
        return ResponseEntity.ok(orderService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> getById(@Positive @PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping(params = "title")
    @Operation(summary = "Get order by title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> getByTitle(@NotBlank @RequestParam String title) {
        return ResponseEntity.ok(orderService.getByTitle(title));
    }

    @GetMapping
    @Operation(summary = "Get orders by filters")
    @ApiResponse(responseCode = "200", description = "Orders returned")
    public ResponseEntity<PagedOrderResponse> getAll(
            @RequestParam(required = false) OrderStatus status,
            @Positive @RequestParam(required = false) Integer clientId,
            @Positive @RequestParam(required = false) Integer translatorId,
            @PageableDefault(sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(
                toPageResponse(orderService.getAll(status, clientId, translatorId, pageable))
        );
    }

    @GetMapping("/search/jpql")
    @Operation(summary = "Complex search with JPQL")
    @ApiResponse(responseCode = "200", description = "Orders returned")
    public ResponseEntity<List<OrderResponse>> searchJpql(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String languageCode
    ) {
        return ResponseEntity.ok(
                orderService.findByStatusAndTranslatorLanguageJpql(
                        status,
                        languageCode
                )
        );
    }

    @GetMapping("/search/native")
    @Operation(summary = "Complex search with native query")
    @ApiResponse(responseCode = "200", description = "Orders returned")
    public ResponseEntity<List<OrderResponse>> searchNative(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String languageCode
    ) {
        return ResponseEntity.ok(
                orderService.findByStatusAndTranslatorLanguageNative(
                        status,
                        languageCode
                )
        );
    }

    @PostMapping("/reports/async")
    @Operation(summary = "Start async order report generation")
    @ApiResponse(responseCode = "200", description = "Task started")
    public ResponseEntity<AsyncTaskCreatedResponse> startAsyncReport(
            @RequestParam(required = false) OrderStatus status,
            @Positive @RequestParam(required = false) Integer clientId,
            @Positive @RequestParam(required = false) Integer translatorId
    ) {
        return ResponseEntity.ok(
                orderAsyncService.startOrderReportTask(status, clientId, translatorId)
        );
    }

    @GetMapping("/tasks/{taskId}")
    @Operation(summary = "Get async task status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task status returned"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    public ResponseEntity<OrderTaskStatusResponse> getTaskStatus(
            @PathVariable String taskId
    ) {
        return ResponseEntity.ok(orderAsyncService.getTaskStatus(taskId));
    }

    @GetMapping("/tasks/stats")
    @Operation(summary = "Get async task counters")
    @ApiResponse(responseCode = "200", description = "Task counters returned")
    public ResponseEntity<OrderAsyncTaskStatsResponse> getTaskStats() {
        return ResponseEntity.ok(orderAsyncService.getTaskStats());
    }

    @PutMapping({"/{id}", "/update/{id}"})
    @Operation(summary = "Update order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> update(
            @Positive @PathVariable Integer id,
            @Valid @RequestBody OrderRequest request
    ) {
        return ResponseEntity.ok(orderService.update(id, request));
    }

    @PatchMapping({"/{id}", "/patch/{id}"})
    @Operation(summary = "Patch order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order patched"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> patch(
            @Positive @PathVariable Integer id,
            @Valid @RequestBody OrderRequest request
    ) {
        return ResponseEntity.ok(orderService.patch(id, request));
    }

    @DeleteMapping({"/{id}", "/delete/{id}"})
    @Operation(summary = "Delete order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order deleted"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> delete(@Positive @PathVariable Integer id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private PagedOrderResponse toPageResponse(Page<OrderResponse> page) {
        return new PagedOrderResponse(
                page.getContent(),
                new PageMeta(
                        page.getSize(),
                        page.getNumber(),
                        page.getTotalElements(),
                        page.getTotalPages()
                )
        );
    }

    public static record PagedOrderResponse(
            List<OrderResponse> content,
            PageMeta page
    ) {
    }

    public static record PageMeta(
            int size,
            int number,
            long totalElements,
            int totalPages
    ) {
    }
}


