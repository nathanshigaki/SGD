package com.govmt.sgd.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.govmt.sgd.dto.request.DocumentoRequest;
import com.govmt.sgd.dto.response.DocumentoResponse;
import com.govmt.sgd.service.DocumentoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/documentos")
@RequiredArgsConstructor
public class DocumentoController {

    private final DocumentoService documentoService;

    @PostMapping
    @PreAuthorize("hasAuthority('DOCUMENTO:CRIAR')")
    public ResponseEntity<DocumentoResponse> create(@Valid @RequestBody DocumentoRequest request) {
        DocumentoResponse response = documentoService.createDocumento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('DOCUMENTO:LER')")
    public ResponseEntity<List<DocumentoResponse>> getAll() {
        return ResponseEntity.ok(documentoService.getAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DOCUMENTO:LER')")
    public ResponseEntity<DocumentoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(documentoService.findById(id));
    }

    @PutMapping
    @PreAuthorize("hasAuthority('DOCUMENTO:ATUALIZAR')")
    public ResponseEntity<DocumentoResponse> update(@Valid @RequestBody DocumentoRequest request) {
        return ResponseEntity.ok(documentoService.updateDocumento(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DOCUMENTO:EXCLUIR')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        documentoService.deleteDocumento(id);
        return ResponseEntity.noContent().build();
    }
}
