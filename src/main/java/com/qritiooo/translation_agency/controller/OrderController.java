package com.qritiooo.translation_agency.controller;

import com.qritiooo.translation_agency.dto.OrderDto;
import com.qritiooo.translation_agency.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderDto> create(@RequestBody OrderDto dto) {
        return ResponseEntity.ok(orderService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getById(@PathVariable Integer id) {
        try { return ResponseEntity.ok(orderService.getById(id)); }
        catch (Exception e) { return ResponseEntity.notFound().build(); }
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer clientId,
            @RequestParam(required = false) Integer translatorId
    ) {
        return ResponseEntity.ok(orderService.getAll(status, clientId, translatorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderDto> update(@PathVariable Integer id, @RequestBody OrderDto dto) {
        return ResponseEntity.ok(orderService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        orderService.delete(id);
        return ResponseEntity.noContent().build();
    }
}


