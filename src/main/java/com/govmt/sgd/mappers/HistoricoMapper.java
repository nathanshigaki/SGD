package com.govmt.sgd.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.govmt.sgd.dto.response.HistoricoResponse;
import com.govmt.sgd.model.Historico;

@Mapper(componentModel = "spring")
public interface HistoricoMapper {

    @Mapping(target = "documento.id", source = "documento.id")
    @Mapping(target = "documento.sigdoc", source = "documento.sigdoc")
    @Mapping(target = "usuario.id", source = "usuario.id")
    @Mapping(target = "usuario.nome", source = "usuario.nome")
    @Mapping(target = "aprovador.id", source = "aprovador.id")
    @Mapping(target = "aprovador.nome", source = "aprovador.nome")
    HistoricoResponse toResponseFromHistorico(Historico historico);
}
