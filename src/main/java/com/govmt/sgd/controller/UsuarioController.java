package com.govmt.sgd.controller;

import java.util.List;
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

import com.govmt.sgd.dto.request.UsuarioRequest;
import com.govmt.sgd.dto.response.UsuarioResponse;
import com.govmt.sgd.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@Tag(name = "Usuários", description = "Operações de cadastro, gerenciamento de contas e controle de permissões de acesso")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    @Operation(
        summary = "Cadastrar novo usuário",
        description = "Registra uma nova conta de usuário no sistema."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos ou e-mail já existente"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - falta de autorização")
    })
    @PreAuthorize("hasAuthority('USUARIO:CRIAR')")
    public ResponseEntity<UsuarioResponse> createUsuario(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.createUsuario(request));
    }

    @GetMapping
    @Operation(
        summary = "Listar todos os usuários",
        description = "Retorna a listagem completa de contas cadastradas (Sem paginação)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listagem de usuários retornada com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado")
    })
    @PreAuthorize("hasAuthority('USUARIO:LER')")
    public ResponseEntity<Page<UsuarioResponse>> getAllUsuarios(
        @Parameter(hidden = true)
        @PageableDefault(size = 10, page = 0, sort = "nome", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(usuarioService.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Buscar usuário por ID",
        description = "Recupera os detalhes do perfil de um usuário específico."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuário encontrado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado na base de dados")
    })
    @PreAuthorize("hasAuthority('USUARIO:LER')")
    public ResponseEntity<UsuarioResponse> findUsuarioById(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.findById(id));
    }

    @PutMapping
    @Operation(
        summary = "Atualizar dados do usuário",
        description = "Atualiza as informações pessoais e cadastrais de um usuário."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dados do usuário atualizados com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PreAuthorize("hasAuthority('USUARIO:ATUALIZAR')")
    public ResponseEntity<UsuarioResponse> update(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.updateUsuario(request));
    }

    @PostMapping("/{id}/permissoes")
    @Operation(
        summary = "Adicionar permissões",
        description = "Adiciona novas permissões para um utilizador específico."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Permissões adicionadas com sucesso"),
        @ApiResponse(responseCode = "400", description = "Corpo da requisição inválido (Lista mal formatada)"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - Operação exclusiva para administradores"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PreAuthorize("hasAuthority('*:*')")
    public ResponseEntity<UsuarioResponse> adicionarPermissoes(
            @PathVariable UUID id, 
            @Valid @RequestBody List<String> permissoes) {
        return ResponseEntity.ok(usuarioService.adicionarPermissoes(id, permissoes));
    }

    @DeleteMapping("/{id}/permissoes")
    @Operation(
        summary = "Remover permissões",
        description = "Remove permissões específicas da conta de um utilizador."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Permissões removidas com sucesso"),
        @ApiResponse(responseCode = "400", description = "Corpo da requisição inválido"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado - Operação exclusiva para administradores"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PreAuthorize("hasAuthority('*:*')")
    public ResponseEntity<UsuarioResponse> removerPermissoes(
            @PathVariable UUID id, 
            @Valid @RequestBody List<String> permissoes) {
        return ResponseEntity.ok(usuarioService.removerPermissoes(id, permissoes));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Excluir conta de usuário",
        description = "Remove fisicamente o registro de um usuário. Ação irreversível."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso (No Content)"),
        @ApiResponse(responseCode = "401", description = "Token ausente ou inválido"),
        @ApiResponse(responseCode = "403", description = "Acesso negado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado"),
        @ApiResponse(responseCode = "500", description = "Erro de integridade (Ex: Usuário está atrelado ao histórico ou a documentos)")
    })
    @PreAuthorize("hasAuthority('USUARIO:EXCLUIR')")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        usuarioService.deleteUsuario(id);
        return ResponseEntity.noContent().build();
    }
}
