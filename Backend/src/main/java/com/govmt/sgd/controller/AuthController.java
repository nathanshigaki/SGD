package com.govmt.sgd.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.govmt.sgd.dto.request.LoginRequest;
import com.govmt.sgd.dto.request.UsuarioRequest;
import com.govmt.sgd.dto.response.UsuarioResponse;
import com.govmt.sgd.service.JwtService;
import com.govmt.sgd.service.UsuarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoint público para login e cadastro de novos usuários")
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    @Operation(
        summary = "Fazer login no sistema",
        description = "Autentica um usuário através de e-mail e senha, retornando o token JWT."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login bem-sucedido, token JWT retornado"),
        @ApiResponse(responseCode = "400", description = "Corpo da requisição inválido"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public String authenticate(@Valid @RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.email(), request.senha());
        
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        
        return jwtService.generateToken(authentication);
    }

    @PostMapping("/cadastrar")
    @Operation(summary = "Cadastrar novo usuário", description = "Registra uma nova conta de usuário padrão no sistema.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados de requisição inválidos ou e-mail já existente")
    })
    public ResponseEntity<UsuarioResponse> createUsuario(@Valid @RequestBody UsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.createUsuario(request));
    }
}
