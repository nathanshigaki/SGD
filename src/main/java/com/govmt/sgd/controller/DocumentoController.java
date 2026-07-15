package com.govmt.sgd.controller;

import java.time.LocalDateTime;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.govmt.sgd.dto.request.DocumentoRequest;
import com.govmt.sgd.dto.response.DocumentoResponse;
import com.govmt.sgd.service.DocumentoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/documentos")
@RequiredArgsConstructor
@Tag(name = "Documentos", description = "Gerenciamento de documentos do sigadoc")
public class DocumentoController {

    private final DocumentoService documentoService;

    @PostMapping
    @Operation(
        summary = "Criar novo documento",
        description = "Cria um documento diretamente (se Admin) ou gera uma solicitação de criação pendente de aprovação."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Documento criado com sucesso"),
        @ApiResponse(responseCode = "202", description = "Solicitação de criação e enviada para aprovação"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição (erro de validação)"),
        @ApiResponse(responseCode = "401", description = "Token de autenticação ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - falta de autorização")
    })
    @PreAuthorize("hasAuthority('DOCUMENTO:CRIAR')")
    public ResponseEntity<DocumentoResponse> create(@Valid @RequestBody DocumentoRequest request) {
        DocumentoResponse response = documentoService.createDocumento(request);
        if (response.id() == null) {
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(
        summary = "Listar todos os documentos",
        description = "Retorna uma listagem global e paginada de todos os documentos sem filtros"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Página de documentos retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - falta de autorização")
    })
    @PreAuthorize("hasAuthority('DOCUMENTO:LER')")
    public ResponseEntity<Page<DocumentoResponse>> getAll(
        @Parameter(hidden = true)
        @PageableDefault(size = 10, page = 0, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(documentoService.getAll(pageable));
    }

    @GetMapping("/buscar")
    @Operation(
        summary = "Filtrar e listar documentos paginados",
        description = "Retorna uma página de documentos de acordo com os critérios informados."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listagem retornada com sucesso"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - falta de autorização"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido")
    })
    @PreAuthorize("hasAuthority('DOCUMENTO:LER')")
    public ResponseEntity<Page<DocumentoResponse>> buscarComFiltros(
            @RequestParam(required = false) String sigdoc,
            @RequestParam(required = false) String situacao,
            @RequestParam(required = false) LocalDateTime chegouEm,
            @RequestParam(required = false) Boolean condes,
            @RequestParam(required = false) String parecerFinal,
            @Parameter(hidden = true)
            @PageableDefault(size = 10, page = 0, sort = "criadoEm", direction = Sort.Direction.DESC) Pageable pageable) {
        
        return ResponseEntity.ok(documentoService.buscarComFiltros(sigdoc, situacao, chegouEm, condes, parecerFinal, pageable));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar documento por ID",
        description = "Recupera os detalhes completos de um documento específico através do seu identificador único (UUID)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Documento encontrado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Documento não encontrado na base de dados")
    })
    @PreAuthorize("hasAuthority('DOCUMENTO:LER')")
    public ResponseEntity<DocumentoResponse> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(documentoService.findById(id));
    }

    @PutMapping
    @Operation(
        summary = "Atualizar documento",
        description = "Atualiza os dados de um documento existente."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Documento atualizado com sucesso"),
        @ApiResponse(responseCode = "202", description = "Solicitação de atualização e enviada para aprovação"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - falta de autorização"),
        @ApiResponse(responseCode = "404", description = "Documento não encontrado para atualização")
    })
    @PreAuthorize("hasAuthority('DOCUMENTO:ATUALIZAR')")
    public ResponseEntity<DocumentoResponse> update(@Valid @RequestBody DocumentoRequest request) {
        DocumentoResponse response = documentoService.updateDocumento(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir documento",
        description = "Remove fisicamente um documento do sistema."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "Pedido de exclusão aceite (Ação enviada para aprovação ou executada com sucesso)"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - falta de autorização"),
        @ApiResponse(responseCode = "404", description = "Documento não encontrado")
    })
    @PreAuthorize("hasAuthority('DOCUMENTO:EXCLUIR')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        documentoService.deleteDocumento(id);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PutMapping("/solicitacoes/{historicoId}/validar")
    @Operation(
        summary = "Validar Solicitação de Alteração (Maker-Checker)", 
        description = "Aprova ou rejeita um pedido de pendente. Se aprovado, as alterações são consolidadas na base de dados. (Exclusivo Admin)"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Solicitação avaliada com sucesso (Aprovada ou Rejeitada)"),
        @ApiResponse(responseCode = "400", description = "A solicitação já foi processada anteriormente"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - Operação exclusiva para administradores (*:*)"),
        @ApiResponse(responseCode = "404", description = "Registo de solicitação (Histórico) não encontrado")
    })
    @PreAuthorize("hasAuthority('*:*')")
    public ResponseEntity<Void> validarSolicitacao(
            @PathVariable UUID historicoId, 
            @RequestParam boolean aprovado) {
        documentoService.validarSolicitacao(historicoId, aprovado);
        return ResponseEntity.noContent().build();
    }
}
