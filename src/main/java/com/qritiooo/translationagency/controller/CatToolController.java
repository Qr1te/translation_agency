package com.qritiooo.translationagency.controller;

import com.qritiooo.translationagency.dto.request.CatToolRequest;
import com.qritiooo.translationagency.dto.response.CatToolResponse;
import com.qritiooo.translationagency.service.CatToolService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cat-tools")
public class CatToolController {

    private final CatToolService service;

    @PostMapping
    @Operation(summary = "Create CAT tool")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CAT tool created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<CatToolResponse> create(@RequestBody CatToolRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update CAT tool")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CAT tool updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "CAT tool not found")
    })
    public ResponseEntity<CatToolResponse> update(
            @PathVariable Integer id,
            @RequestBody CatToolRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch CAT tool")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CAT tool patched"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "CAT tool not found")
    })
    public ResponseEntity<CatToolResponse> patch(
            @PathVariable Integer id,
            @RequestBody CatToolRequest request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get CAT tool by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "CAT tool found"),
            @ApiResponse(responseCode = "404", description = "CAT tool not found")
    })
    public ResponseEntity<CatToolResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all CAT tools")
    @ApiResponse(responseCode = "200", description = "CAT tools returned")
    public ResponseEntity<List<CatToolResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete CAT tool")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "CAT tool deleted"),
            @ApiResponse(responseCode = "404", description = "CAT tool not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
