package com.govmt.sgd.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UsuarioResponse(
    UUID id,
    String nome,
    String email,
    String senha,
    List<String> permissao,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {}
