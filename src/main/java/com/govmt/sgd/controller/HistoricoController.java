package com.govmt.sgd.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.govmt.sgd.dto.response.HistoricoResponse;
import com.govmt.sgd.service.HistoricoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/historico")
@RequiredArgsConstructor
public class HistoricoController {

    private final HistoricoService historicoService;

    @GetMapping
    @PreAuthorize("hasAuthority('HISTORICO:LER')") 
    public ResponseEntity<Page<HistoricoResponse>> getAll(
        @PageableDefault(size = 10, page = 0, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ResponseEntity.ok(historicoService.getAll(pageable));
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasAuthority('HISTORICO:LER')") 
    public ResponseEntity<Page<HistoricoResponse>> pesquisarHistorico(
        @RequestParam(required = false) UUID documentoId,
        @RequestParam(required = false) UUID usuarioId,
        @RequestParam(required = false) UUID aprovadorId,
        @RequestParam(required = false) String situacao,
        @RequestParam(required = false) LocalDateTime dataInicio,
        @RequestParam(required = false) LocalDateTime dataFim,
        @PageableDefault(size = 10, page = 0, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable) 
    {    
        return ResponseEntity.ok(historicoService.buscarHistoricoComFiltros(documentoId, usuarioId, aprovadorId, situacao, dataInicio, dataFim, pageable));
    }
}
