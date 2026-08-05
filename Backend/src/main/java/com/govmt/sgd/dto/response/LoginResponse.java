package com.govmt.sgd.dto.response;

import java.util.Set;
import java.util.UUID;

public record LoginResponse(
    String token,
    Long expiresIn, 
    UUID id,
    String nome,
    String email,
    Set<String> permissoes
) {}