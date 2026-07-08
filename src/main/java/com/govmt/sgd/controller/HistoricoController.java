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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/historico")
@RequiredArgsConstructor
@Tag(name = "Históricos (Auditoria)", description = "Logs e rastreabilidade imutável de eventos do sistema")
public class HistoricoController {

    private final HistoricoService historicoService;

    @GetMapping
    @Operation(
        summary = "Listagem de auditoria global",
        description = "Retorna de forma paginada todas as ações executadas no ecossistema da aplicação."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Histórico geral retornado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - falta de autorização")
    })
    @PreAuthorize("hasAuthority('HISTORICO:LER')") 
    public ResponseEntity<Page<HistoricoResponse>> getAll(
        @Parameter(hidden = true)
        @PageableDefault(size = 10, page = 0, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable
    ){
        return ResponseEntity.ok(historicoService.getAll(pageable));
    }

    @GetMapping("/buscar")
    @Operation(
        summary = "Pesquisa avançada de logs com filtros combinados",
        description = "Permite filtrar registros de histórico cruzando dados de documentos, autores, aprovadores e intervalos de datas. Rota protegida por 'HISTORICO:LER'."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listagem filtrada retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - falta de autorização")
    })
    @PreAuthorize("hasAuthority('HISTORICO:LER')") 
    public ResponseEntity<Page<HistoricoResponse>> pesquisarHistorico(
        @RequestParam(required = false) UUID documentoId,
        @RequestParam(required = false) UUID usuarioId,
        @RequestParam(required = false) UUID aprovadorId,
        @RequestParam(required = false) String situacao,
        @RequestParam(required = false) LocalDateTime dataInicio,
        @RequestParam(required = false) LocalDateTime dataFim,
        @Parameter(hidden = true)
        @PageableDefault(size = 10, page = 0, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable) 
    {    
        return ResponseEntity.ok(historicoService.buscarHistoricoComFiltros(documentoId, usuarioId, aprovadorId, situacao, dataInicio, dataFim, pageable));
    }
}
