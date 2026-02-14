package com.qritiooo.translation_agency.controller;

import com.qritiooo.translation_agency.dto.DocumentDto;
import com.qritiooo.translation_agency.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService service;

    @PostMapping
    public ResponseEntity<DocumentDto> create(@RequestBody DocumentDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DocumentDto> update(@PathVariable Integer id, @RequestBody DocumentDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getById(@PathVariable Integer id) {
        try { return ResponseEntity.ok(service.getById(id)); }
        catch (Exception e) { return ResponseEntity.notFound().build(); }
    }

    @GetMapping
    public ResponseEntity<List<DocumentDto>> getAll(@RequestParam(required = false) Integer orderId) {
        return ResponseEntity.ok(service.getAll(orderId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

