package com.govmt.sgd.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

import com.govmt.sgd.dto.ValoresHistorico;

public record HistoricoResponse(
    UUID id,
    DocumentoInfo documento,
    UsuarioInfo usuario,
    UsuarioInfo aprovador,
    String situacao,
    String acao,
    ValoresHistorico valores,
    LocalDateTime criadoEm
) {
    public record DocumentoInfo(UUID id, String sigdoc) {}
    public record UsuarioInfo(UUID id, String nome) {}
}
