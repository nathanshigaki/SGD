package com.govmt.sgd.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record FeriadoResponse(
    UUID id,
    LocalDate data,
    LocalDateTime criadoEm
) {}
