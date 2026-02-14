package com.qritiooo.translation_agency.controller;

import com.qritiooo.translation_agency.dto.TranslatorDto;
import com.qritiooo.translation_agency.service.TranslatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/translators")
public class TranslatorController {

    private final TranslatorService service;

    @PostMapping
    public ResponseEntity<TranslatorDto> create(@RequestBody TranslatorDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TranslatorDto> update(@PathVariable Integer id, @RequestBody TranslatorDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TranslatorDto> getById(@PathVariable Integer id) {
        try { return ResponseEntity.ok(service.getById(id)); }
        catch (Exception e) { return ResponseEntity.notFound().build(); }
    }

    @GetMapping
    public ResponseEntity<List<TranslatorDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
