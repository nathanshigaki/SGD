package com.govmt.sgd.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record DocumentoUsuarioRequest(
    UUID id,

    @NotNull
    UUID documentoId,
    
    @NotNull
    UUID usuarioId,
    String cargo,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {}
