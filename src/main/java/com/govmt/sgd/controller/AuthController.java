package com.govmt.sgd.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.govmt.sgd.dto.request.LoginRequest;
import com.govmt.sgd.service.JwtService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoint público para login e emissão de tokens de segurança (JWT)")
public class AuthController {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping
    @Operation(
        summary = "Fazer login no sistema",
        description = "Autentica um usuário através de e-mail e senha. Em caso de sucesso, retorna o token JWT em formato String para ser utilizado no cabeçalho Authorization (Bearer) das requisições subsequentes."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login bem-sucedido, token JWT retornado"),
        @ApiResponse(responseCode = "400", description = "Corpo da requisição inválido (ex: formato de e-mail incorreto)"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas (E-mail ou senha incorretos)")
    })
    public String authenticate(@Valid @RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.email(), request.senha());
        
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        
        return jwtService.generateToken(authentication);
    }
}
