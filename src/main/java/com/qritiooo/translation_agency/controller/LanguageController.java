package com.qritiooo.translation_agency.controller;

import com.qritiooo.translation_agency.dto.request.LanguageRequest;
import com.qritiooo.translation_agency.dto.response.LanguageResponse;
import com.qritiooo.translation_agency.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/languages")
public class LanguageController {

    private final LanguageService service;

    @PostMapping
    @Operation(summary = "Create language")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Language created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<LanguageResponse> create(@RequestBody LanguageRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update language")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Language updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Language not found")
    })
    public ResponseEntity<LanguageResponse> update(@PathVariable Integer id, @RequestBody LanguageRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get language by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Language found"),
            @ApiResponse(responseCode = "404", description = "Language not found")
    })
    public ResponseEntity<LanguageResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all languages")
    @ApiResponse(responseCode = "200", description = "Languages returned")
    public ResponseEntity<List<LanguageResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete language")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Language deleted"),
            @ApiResponse(responseCode = "404", description = "Language not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
