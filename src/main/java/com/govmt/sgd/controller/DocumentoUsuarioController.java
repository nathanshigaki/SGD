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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/documento-usuarios")
@RequiredArgsConstructor
@Tag(name = "Atribuições de Documentos", description = "Vinculação e gerenciamento de cargos atribuídos aos documentos")
public class DocumentoUsuarioController {

    private final DocumentoUsuarioService documentoUsuarioService;

    @PostMapping
    @Operation(
        summary = "Vincular usuário a documento",
        description = "Cria uma nova atribuição conectando um usuário a um documento através de um cargo específico."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Vínculo criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição (erro de validação)"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - falta de autorização"),
        @ApiResponse(responseCode = "404", description = "Documento ou Usuário não encontrado")
    })
    @PreAuthorize("hasAuthority('DOCUMENTO_USUARIO:CRIAR')")
    public ResponseEntity<DocumentoUsuarioResponse> create(@Valid @RequestBody DocumentoUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentoUsuarioService.createDocumentoUsuario(request));
    }

    @GetMapping
    @Operation(
        summary = "Listar todas as atribuições",
        description = "Retorna uma listagem global e paginada de todos os vínculos de usuários com documentos."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Página de atribuições retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - falta de autorização")
    })
    @PreAuthorize("hasAuthority('DOCUMENTO_USUARIO:LER')")
    public ResponseEntity<Page<DocumentoUsuarioResponse>> getAll(
        @Parameter(hidden = true)
        @PageableDefault(size = 10, page = 0, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(documentoUsuarioService.getAll(pageable));
    }

    @GetMapping("/buscar")
    @Operation(
        summary = "Pesquisar atribuições com filtros",
        description = "Busca vínculos específicos filtrando por documento, por usuário ou por cargo."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listagem filtrada retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - falta de autorização")
    })
    @PreAuthorize("hasAuthority('LER_DOCUMENTO')")
    public ResponseEntity<Page<DocumentoUsuarioResponse>> buscarComFiltros(
            @RequestParam(required = false) UUID documentoId,
            @RequestParam(required = false) UUID usuarioId,
            @RequestParam(required = false) String cargo,
            @Parameter(hidden = true)
            @PageableDefault(size = 10, page = 0, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable) {
        
        return ResponseEntity.ok(documentoUsuarioService.buscarComFiltros(documentoId, usuarioId, cargo, pageable));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar vínculo por ID",
        description = "Recupera os detalhes de uma atribuição específica através do seu identificador único (UUID)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Vínculo encontrado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Atribuição não encontrada na base de dados")
    })
    @PreAuthorize("hasAuthority('DOCUMENTO_USUARIO:LER')")
    public ResponseEntity<DocumentoUsuarioResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(documentoUsuarioService.findById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Remover vínculo (Excluir atribuição)",
        description = "Desvincula fisicamente um usuário de um documento. Ação irreversível. Rota protegida por 'DOCUMENTO_USUARIO:EXCLUIR'."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Vínculo removido com sucesso (No Content)"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - falta de autorização"),
        @ApiResponse(responseCode = "404", description = "Atribuição não encontrada")
    })
    @PreAuthorize("hasAuthority('DOCUMENTO_USUARIO:EXCLUIR')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        documentoUsuarioService.deleteDocumentoUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
