package com.qritiooo.translationagency.controller;

import com.qritiooo.translationagency.api.validation.OnCreate;
import com.qritiooo.translationagency.api.validation.OnPatch;
import com.qritiooo.translationagency.api.validation.OnUpdate;
import com.qritiooo.translationagency.dto.request.OrderRequest;
import com.qritiooo.translationagency.dto.response.OrderResponse;
import com.qritiooo.translationagency.model.OrderStatus;
import com.qritiooo.translationagency.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    @PostMapping({"", "/create"})
    @Operation(summary = "Create order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<OrderResponse> create(
            @Validated(OnCreate.class) @RequestBody OrderRequest request
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
    public ResponseEntity<List<OrderResponse>> getAll(
            @RequestParam(required = false) OrderStatus status,
            @Positive @RequestParam(required = false) Integer clientId,
            @Positive @RequestParam(required = false) Integer translatorId
    ) {
        return ResponseEntity.ok(orderService.getAll(status, clientId, translatorId));
    }

    @GetMapping("/search/jpql")
    @Operation(summary = "Complex search with JPQL and pagination")
    @ApiResponse(responseCode = "200", description = "Orders returned")
    public ResponseEntity<Map<String, Object>> searchJpql(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String languageCode,
            @PageableDefault(sort = "id") Pageable pageable
    ) {
        Page<OrderResponse> result = orderService.searchByNestedJpql(status, languageCode, pageable);
        return ResponseEntity.ok(toPageResponse(result));
    }

    @GetMapping("/search/native")
    @Operation(summary = "Complex search with native query and pagination")
    @ApiResponse(responseCode = "200", description = "Orders returned")
    public ResponseEntity<Map<String, Object>> searchNative(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String languageCode,
            @PageableDefault(sort = "id") Pageable pageable
    ) {
        Page<OrderResponse> result = orderService.searchByNestedNative(status, languageCode, pageable);
        return ResponseEntity.ok(toPageResponse(result));
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
            @Validated(OnUpdate.class) @RequestBody OrderRequest request
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
            @Validated(OnPatch.class) @RequestBody OrderRequest request
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

    private Map<String, Object> toPageResponse(Page<OrderResponse> page) {
        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, Object> pageMeta = new LinkedHashMap<>();

        pageMeta.put("size", page.getSize());
        pageMeta.put("number", page.getNumber());
        pageMeta.put("totalElements", page.getTotalElements());
        pageMeta.put("totalPages", page.getTotalPages());

        response.put("content", page.getContent());
        response.put("page", pageMeta);
        return response;
    }
}


