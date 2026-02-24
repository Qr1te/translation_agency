package com.qritiooo.translationagency.controller;

import com.qritiooo.translationagency.dto.request.TranslatorRequest;
import com.qritiooo.translationagency.dto.response.TranslatorResponse;
import com.qritiooo.translationagency.service.TranslatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/translators")
public class TranslatorController {

    private final TranslatorService service;

    @PostMapping
    @Operation(summary = "Create translator")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Translator created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<TranslatorResponse> create(@RequestBody TranslatorRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update translator")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Translator updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Translator not found")
    })
    public ResponseEntity<TranslatorResponse> update(
            @PathVariable Integer id,
            @RequestBody TranslatorRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get translator by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Translator found"),
            @ApiResponse(responseCode = "404", description = "Translator not found")
    })
    public ResponseEntity<TranslatorResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all translators")
    @ApiResponse(responseCode = "200", description = "Translators returned")
    public ResponseEntity<List<TranslatorResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete translator")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Translator deleted"),
            @ApiResponse(responseCode = "404", description = "Translator not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
