package com.govmt.sgd.dto.response;

import java.util.List;
import java.util.UUID;

public record LoginResponse(
    String token,
    Long expiresIn, 
    UUID id,
    String nome,
    String email,
    List<String> permissoes
) {}