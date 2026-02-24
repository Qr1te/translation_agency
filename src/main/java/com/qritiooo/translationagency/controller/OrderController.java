package com.qritiooo.translationagency.controller;

import com.qritiooo.translationagency.dto.request.OrderRequest;
import com.qritiooo.translationagency.dto.response.OrderResponse;
import com.qritiooo.translationagency.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/create")
    @Operation(summary = "Create order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<OrderResponse> create(@RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.create(request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping(params = "title")
    @Operation(summary = "Get order by title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> getByTitle(@RequestParam String title) {
        return ResponseEntity.ok(orderService.getByTitle(title));
    }

    @GetMapping
    @Operation(summary = "Get orders by filters")
    @ApiResponse(responseCode = "200", description = "Orders returned")
    public ResponseEntity<List<OrderResponse>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer clientId,
            @RequestParam(required = false) Integer translatorId
    ) {
        return ResponseEntity.ok(orderService.getAll(status, clientId, translatorId));
    }

    @GetMapping("/search/jpql")
    @Operation(summary = "Complex search with JPQL and pagination")
    @ApiResponse(responseCode = "200", description = "Orders returned")
    public ResponseEntity<Page<OrderResponse>> searchJpql(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String languageCode,
            @PageableDefault(sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(orderService.searchByNestedJpql(status, languageCode, pageable));
    }

    @GetMapping("/search/native")
    @Operation(summary = "Complex search with native query and pagination")
    @ApiResponse(responseCode = "200", description = "Orders returned")
    public ResponseEntity<Page<OrderResponse>> searchNative(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String languageCode,
            @PageableDefault(sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(
                orderService.searchByNestedNative(status, languageCode, pageable)
        );
    }

    @PutMapping("/update/{id}")
    @Operation(summary = "Update order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<OrderResponse> update(
            @PathVariable Integer id,
            @RequestBody OrderRequest request
    ) {
        return ResponseEntity.ok(orderService.update(id, request));
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "Delete order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order deleted"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


