package com.govmt.sgd.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

import com.govmt.sgd.dto.request.FeriadoRequest;
import com.govmt.sgd.dto.response.FeriadoResponse;
import com.govmt.sgd.service.FeriadoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/feriados")
@RequiredArgsConstructor
@Tag(name = "Feriados", description = "Operações de cadastro e gerenciamento de datas de feriados")
public class FeriadoController {

    private final FeriadoService feriadoService;

    @PostMapping
    @Operation(summary = "Cadastrar novo feriado", description = "Registra uma nova data de feriado no sistema.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Feriado cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição (erro de validação)"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - falta de autorização")
    })
    @PreAuthorize("hasAuthority('*:*')")
    public ResponseEntity<FeriadoResponse> create(@Valid @RequestBody FeriadoRequest request) {
        FeriadoResponse response = feriadoService.createFeriado(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listar todos os feriados", description = "Retorna a listagem de todas as datas de feriados (Sem paginação).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listagem retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PreAuthorize("hasAuthority('*:*')")
    public ResponseEntity<Page<FeriadoResponse>> getAll(
        @Parameter(hidden = true)
        @PageableDefault(size = 10, page = 0, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(feriadoService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar feriado por ID", description = "Recupera os detalhes de um feriado específico através do identificador.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Feriado encontrado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Feriado não encontrado")
    })
    @PreAuthorize("hasAuthority('*:*')")
    public ResponseEntity<FeriadoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(feriadoService.findById(id));
    }

    @PutMapping
    @Operation(summary = "Atualizar feriado", description = "Atualiza a data de um feriado existente.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Feriado atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Feriado não encontrado para atualização")
    })
    @PreAuthorize("hasAuthority('*:*')")
    public ResponseEntity<FeriadoResponse> update(@Valid @RequestBody FeriadoRequest request) {
        return ResponseEntity.ok(feriadoService.updateFeriado(request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir feriado", description = "Remove fisicamente um feriado da base de dados.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Feriado excluído com sucesso (No Content)"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Feriado não encontrado")
    })
    @PreAuthorize("hasAuthority('*:*')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        feriadoService.deleteFeriado(id);
        return ResponseEntity.noContent().build();
    }
}
