package com.govmt.sgd.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DocumentoRequest(
    UUID id, 

    @NotNull
    UUID orgaoId,

    @NotBlank(message = "O identificador é obrigatório")
    @Pattern(
        regexp = "^[a-zA-Z]{3,5}-[a-zA-Z]{3}-\\d{4}/\\d{5}(-[a-zA-Z0-9]+)?$", 
        message = "O formato deve ser XXX-XXX-0000/00000"
    )
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
