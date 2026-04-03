package com.qritiooo.translationagency.controller;

import com.qritiooo.translationagency.dto.response.RaceConditionDemoResponse;
import com.qritiooo.translationagency.service.ConcurrencyDemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/concurrency")
@Tag(name = "Concurrency", description = "Concurrency demo endpoints")
public class ConcurrencyController {

    private final ConcurrencyDemoService concurrencyDemoService;

    @GetMapping("/race-condition/unsafe")
    @Operation(summary = "Demonstrate race condition with unsafe counter")
    @ApiResponse(responseCode = "200", description = "Unsafe race condition demo result")
    public ResponseEntity<RaceConditionDemoResponse> demonstrateUnsafeCounter() {
        return ResponseEntity.ok(concurrencyDemoService.demonstrateUnsafeCounter());
    }

    @GetMapping("/race-condition/atomic")
    @Operation(summary = "Demonstrate race condition solution with AtomicInteger")
    @ApiResponse(responseCode = "200", description = "Atomic counter demo result")
    public ResponseEntity<RaceConditionDemoResponse> demonstrateAtomicCounter() {
        return ResponseEntity.ok(concurrencyDemoService.demonstrateAtomicCounter());
    }
}
