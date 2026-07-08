package com.govmt.sgd.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioRequest(
    UUID id,

    @NotBlank
    String nome,

    @NotBlank
    @Email(message = "Email inválido")
    String email,

    @NotBlank
    String senha,

    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {}
