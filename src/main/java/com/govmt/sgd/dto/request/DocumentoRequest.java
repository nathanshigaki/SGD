package com.govmt.sgd.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

public record DocumentoRequest(
    UUID id, 
    UUID orgaoId,
    String sigdoc, 
    Date chegouEm,  
    Date concluiuEm,   
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
