package com.qritiooo.translationagency.controller;

import com.qritiooo.translationagency.dto.response.LanguageResponse;
import com.qritiooo.translationagency.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/languages")
public class LanguageController {

    private final LanguageService service;

    @GetMapping("/{code}")
    @Operation(summary = "Get language by code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Language found"),
            @ApiResponse(responseCode = "404", description = "Language not found")
    })
    public ResponseEntity<LanguageResponse> getByCode(@PathVariable String code) {
        return ResponseEntity.ok(service.getByCode(code));
    }

    @GetMapping
    @Operation(summary = "Get all languages")
    @ApiResponse(responseCode = "200", description = "Languages returned")
    public ResponseEntity<List<LanguageResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}
