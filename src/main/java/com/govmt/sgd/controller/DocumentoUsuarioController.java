package com.govmt.sgd.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.govmt.sgd.dto.request.DocumentoUsuarioRequest;
import com.govmt.sgd.dto.response.DocumentoUsuarioResponse;
import com.govmt.sgd.service.DocumentoUsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/documento-usuarios")
@RequiredArgsConstructor
public class DocumentoUsuarioController {

    private final DocumentoUsuarioService documentoUsuarioService;

    @PostMapping
    public ResponseEntity<DocumentoUsuarioResponse> create(@RequestBody DocumentoUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentoUsuarioService.createDocumentoUsuario(request));
    }

    @GetMapping
    public ResponseEntity<List<DocumentoUsuarioResponse>> getAll() {
        return ResponseEntity.ok(documentoUsuarioService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentoUsuarioResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(documentoUsuarioService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        documentoUsuarioService.deleteDocumentoUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
