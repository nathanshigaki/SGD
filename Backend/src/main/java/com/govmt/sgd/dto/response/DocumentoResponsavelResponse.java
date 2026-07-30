package com.govmt.sgd.dto.response;

import java.util.UUID;

public record DocumentoResponsavelResponse(
    UUID usuarioId, 
    String nome, 
    String cargo
) {

}
