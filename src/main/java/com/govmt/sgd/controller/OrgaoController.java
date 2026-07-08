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

import com.govmt.sgd.dto.request.OrgaoRequest;
import com.govmt.sgd.dto.response.OrgaoResponse;
import com.govmt.sgd.service.OrgaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orgaos")
@RequiredArgsConstructor
@Tag(name = "Órgãos", description = "Cadastro de órgãos institucionais vinculadas aos documentos")
public class OrgaoController {

    private final OrgaoService orgaoService;

    @PostMapping
    @Operation(
        summary = "Cadastrar novo órgão",
        description = "Registra um novo órgão ou secretaria no sistema."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Órgão cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição (erro de validação)"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - falta de autorização global")
    })
    @PreAuthorize("hasAuthority('*:*')")
    public ResponseEntity<OrgaoResponse> create(@Valid @RequestBody OrgaoRequest request) {
        OrgaoResponse response = orgaoService.createOrgao(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
        summary = "Listar todos os órgãos",
        description = "Retorna a listagem completa de todos os órgãos cadastrados no sistema (Sem paginação)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listagem de órgãos retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PreAuthorize("hasAuthority('*:*')")
    public ResponseEntity<List<OrgaoResponse>> getAll() {
        return ResponseEntity.ok(orgaoService.getAll());
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar órgão por ID",
        description = "Recupera os detalhes de um órgão específico através do seu identificador único (UUID)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Órgão encontrado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Órgão não encontrado na base de dados")
    })
    @PreAuthorize("hasAuthority('*:*')")
    public ResponseEntity<OrgaoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(orgaoService.findById(id));
    }

    @PutMapping
    @Operation(
        summary = "Atualizar órgão",
        description = "Atualiza as informações de um órgão existente."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Órgão atualizado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Órgão não encontrado para atualização")
    })
    @PreAuthorize("hasAuthority('*:*')")
    public ResponseEntity<OrgaoResponse> update(@Valid @RequestBody OrgaoRequest request) {
        return ResponseEntity.ok(orgaoService.updateOrgao(request));
    }


    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir órgão",
        description = "Remove fisicamente um órgão do sistema. Ação irreversível e sujeita a restrições de chave estrangeira."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Órgão excluído com sucesso (No Content)"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Órgão não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro de integridade (Ex: Órgão possui usuários vinculados)")
    })
    @PreAuthorize("hasAuthority('*:*')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        orgaoService.deleteOrgao(id);
        return ResponseEntity.noContent().build();
    }
}
