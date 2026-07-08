package com.govmt.sgd.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.govmt.sgd.dto.response.HistoricoResponse;
import com.govmt.sgd.model.Historico;
import com.govmt.sgd.model.Usuario;

@Mapper(componentModel = "spring")
public interface HistoricoMapper {

    @Mapping(target = "documento.id", source = "documento.id")
    @Mapping(target = "documento.sigdoc", source = "documento.sigdoc")
    @Mapping(target = "aprovador.id", source = "aprovador.id")
    @Mapping(target = "aprovador.nome", source = "aprovador.nome")
    @Mapping(target = "usuario", expression = "java(mapUsuarioInfo(historico.getUsuario()))")
    HistoricoResponse toResponseFromHistorico(Historico historico);

    default HistoricoResponse.UsuarioInfo mapUsuarioInfo(Usuario usuario) {
        if (usuario == null) {
            return new HistoricoResponse.UsuarioInfo(null, "SISTEMA");
        }
        return new HistoricoResponse.UsuarioInfo(usuario.getId(), usuario.getNome());
    }
}
