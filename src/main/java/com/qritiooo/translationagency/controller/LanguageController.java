package com.qritiooo.translationagency.controller;

import com.qritiooo.translationagency.dto.request.LanguageRequest;
import com.qritiooo.translationagency.dto.response.LanguageResponse;
import com.qritiooo.translationagency.service.LanguageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.util.List;
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
@RequestMapping("/api/languages")
@Validated
@Tag(name = "Languages", description = "Language management endpoints")
public class LanguageController {

    private final LanguageService service;

    @PostMapping
    @Operation(summary = "Create language")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Language created"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "409", description = "Conflict")
    })
    public ResponseEntity<LanguageResponse> create(
            @Valid @RequestBody LanguageRequest request
    ) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update language")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Language updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Language not found"),
            @ApiResponse(responseCode = "409", description = "Conflict")
    })
    public ResponseEntity<LanguageResponse> update(
            @Positive @PathVariable Integer id,
            @Valid @RequestBody LanguageRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Patch language")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Language patched"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Language not found"),
            @ApiResponse(responseCode = "409", description = "Conflict")
    })
    public ResponseEntity<LanguageResponse> patch(
            @Positive @PathVariable Integer id,
            @Valid @RequestBody LanguageRequest request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get language by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Language found"),
            @ApiResponse(responseCode = "404", description = "Language not found")
    })
    public ResponseEntity<LanguageResponse> getById(@Positive @PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping(params = "code")
    @Operation(summary = "Get language by code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Language found"),
            @ApiResponse(responseCode = "404", description = "Language not found")
    })
    public ResponseEntity<LanguageResponse> getByCode(@NotBlank @RequestParam String code) {
        return ResponseEntity.ok(service.getByCode(code));
    }

    @GetMapping
    @Operation(summary = "Get all languages")
    @ApiResponse(responseCode = "200", description = "Languages returned")
    public ResponseEntity<PagedLanguageResponse> getAll(
            @PageableDefault(sort = "id") Pageable pageable
    ) {
        return ResponseEntity.ok(toPageResponse(service.getAll(pageable)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete language")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Language deleted"),
            @ApiResponse(responseCode = "404", description = "Language not found")
    })
    public ResponseEntity<Void> delete(@Positive @PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    private PagedLanguageResponse toPageResponse(Page<LanguageResponse> page) {
        return new PagedLanguageResponse(
                page.getContent(),
                new PageMeta(
                        page.getSize(),
                        page.getNumber(),
                        page.getTotalElements(),
                        page.getTotalPages()
                )
        );
    }

    public static record PagedLanguageResponse(
            List<LanguageResponse> content,
            PageMeta page
    ) {
    }

    public static record PageMeta(
            int size,
            int number,
            long totalElements,
            int totalPages
    ) {
    }
}
