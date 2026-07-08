package com.govmt.sgd.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.govmt.sgd.dto.request.DocumentoUsuarioRequest;
import com.govmt.sgd.dto.response.DocumentoUsuarioResponse;
import com.govmt.sgd.service.DocumentoUsuarioService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/documento-usuarios")
@RequiredArgsConstructor
public class DocumentoUsuarioController {

    private final DocumentoUsuarioService documentoUsuarioService;

    @PostMapping
    @PreAuthorize("hasAuthority('DOCUMENTO_USUARIO:CRIAR')")
    public ResponseEntity<DocumentoUsuarioResponse> create(@Valid @RequestBody DocumentoUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentoUsuarioService.createDocumentoUsuario(request));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('DOCUMENTO_USUARIO:LER')")
    public ResponseEntity<Page<DocumentoUsuarioResponse>> getAll(
        @PageableDefault(size = 10, page = 0, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(documentoUsuarioService.getAll(pageable));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAuthority('LER_DOCUMENTO')")
    public ResponseEntity<Page<DocumentoUsuarioResponse>> buscarComFiltros(
            @RequestParam(required = false) UUID documentoId,
            @RequestParam(required = false) UUID usuarioId,
            @RequestParam(required = false) String cargo,
            @PageableDefault(size = 10, page = 0, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable) {
        
        return ResponseEntity.ok(documentoUsuarioService.buscarComFiltros(documentoId, usuarioId, cargo, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DOCUMENTO_USUARIO:LER')")
    public ResponseEntity<DocumentoUsuarioResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(documentoUsuarioService.findById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DOCUMENTO_USUARIO:EXCLUIR')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        documentoUsuarioService.deleteDocumentoUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
