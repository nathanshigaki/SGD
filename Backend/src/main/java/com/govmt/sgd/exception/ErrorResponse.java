package com.govmt.sgd.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
    LocalDateTime timestamp,
    Integer status,
    String erro,
    String mensagem,
    String caminho
) {}
