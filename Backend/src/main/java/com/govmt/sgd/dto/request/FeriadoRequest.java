package com.govmt.sgd.dto.request;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record FeriadoRequest(
    UUID id,

    @NotNull(message = "A data do feriado é obrigatória")
    LocalDate data
) {}
