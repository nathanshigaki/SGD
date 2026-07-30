package com.govmt.sgd.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.govmt.sgd.dto.request.UsuarioRequest;
import com.govmt.sgd.dto.response.UsuarioResponse;
import com.govmt.sgd.model.Usuario;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "permissoes", ignore = true)
    @Mapping(target = "deletadoEm", ignore = true)
    Usuario toUsuarioFromRequest(UsuarioRequest request);
    UsuarioRequest toRequestFromUsuario(Usuario usuario);
    
    @Mapping(target = "senha", ignore = true)
    @Mapping(target = "deletadoEm", ignore = true)
    @Mapping(target = "permissoes", ignore = true)
    Usuario toUsuarioFromResponse(UsuarioResponse response);

    @Mapping(target = "permissoes", ignore = true)
    UsuarioResponse toResponseFromUsuario(Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", ignore = true)
    @Mapping(target = "senha", ignore = true)
    @Mapping(target = "permissoes", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "deletadoEm", ignore = true)
    void updateUsuarioFromRequest(UsuarioRequest request, @MappingTarget Usuario entity);
}
