package com.govmt.sgd.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record OrgaoRequest(
    UUID id,

    @NotBlank
    String nome,

    @NotBlank
    String acronimo,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {}
