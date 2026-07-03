package com.govmt.sgd.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UsuarioRequest(
    UUID id,

    @NotBlank
    String nome,

    @NotBlank
    @Email(message = "Email inválido")
    String email,

    @NotBlank
    String senha,

    @NotNull
    List<String> permissao,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {}
