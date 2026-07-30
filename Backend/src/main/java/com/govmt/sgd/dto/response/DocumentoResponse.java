package com.govmt.sgd.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


public record DocumentoResponse(
    UUID id, 
    OrgaoResponse orgao,
    List<DocumentoResponsavelResponse> responsaveis,
    String sigdoc, 
    LocalDateTime chegouEm,  
    LocalDateTime concluiuEm,   
    int emEspera,
    BigDecimal valor,  
    String situacao,
    String caracterizacaoTi,   
    Boolean iniciado,  
    Boolean condes, 
    String resumo, 
    String tipoContratacao,   
    String objeto,
    String recomendacao,
    String parecerFinal,
    LocalDateTime deletadoEm,
    LocalDateTime criadoEm,
    LocalDateTime atualizadoEm
) {}
