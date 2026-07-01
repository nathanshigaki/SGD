package com.govmt.sgd.dto.request;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record UsuarioRequest(
    UUID id,
    String nome,
    String email,
    String senha,
    List<String> permissao,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {}
