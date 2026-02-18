package com.qritiooo.translation_agency.controller;

import com.qritiooo.translation_agency.dto.request.DocumentRequest;
import com.qritiooo.translation_agency.dto.response.DocumentResponse;
import com.qritiooo.translation_agency.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Create document")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<DocumentResponse> create(@RequestBody DocumentRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update document")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    public ResponseEntity<DocumentResponse> update(@PathVariable Integer id, @RequestBody DocumentRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get document by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Document found"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    public ResponseEntity<DocumentResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get documents")
    @ApiResponse(responseCode = "200", description = "Documents returned")
    public ResponseEntity<List<DocumentResponse>> getAll(@RequestParam(required = false) Integer orderId) {
        return ResponseEntity.ok(service.getAll(orderId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete document")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Document deleted"),
            @ApiResponse(responseCode = "404", description = "Document not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

