package com.govmt.sgd.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentoUsuarioRequest(
    UUID id,
    UUID documentoId,
    UUID usuarioId,
    String cargo,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {}
