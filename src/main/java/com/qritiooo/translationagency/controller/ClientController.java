package com.qritiooo.translationagency.controller;

import com.qritiooo.translationagency.dto.request.ClientRequest;
import com.qritiooo.translationagency.dto.response.ClientResponse;
import com.qritiooo.translationagency.service.ClientService;
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
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService service;

    @PostMapping
    @Operation(summary = "Create client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client created"),
            @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public ResponseEntity<ClientResponse> create(@RequestBody ClientRequest request) {
        return ResponseEntity.ok(service.create(request));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ResponseEntity<ClientResponse> update(
            @PathVariable Integer id,
            @RequestBody ClientRequest request
    ) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get client by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client found"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ResponseEntity<ClientResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get all clients")
    @ApiResponse(responseCode = "200", description = "Clients returned")
    public ResponseEntity<List<ClientResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Client deleted"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
