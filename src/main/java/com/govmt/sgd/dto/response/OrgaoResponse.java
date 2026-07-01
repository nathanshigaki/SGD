package com.govmt.sgd.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record OrgaoResponse(
    UUID id,
    String nome,
    String acronimo,
    LocalDateTime criado_em,
    LocalDateTime atualizado_em
) {}
