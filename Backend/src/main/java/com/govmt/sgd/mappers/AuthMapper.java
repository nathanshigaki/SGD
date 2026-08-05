package com.govmt.sgd.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.govmt.sgd.dto.response.LoginResponse;
import com.govmt.sgd.model.Usuario;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    @Mapping(target = "token", source = "token")
    @Mapping(target = "expiresIn", source = "expiresIn")
    @Mapping(target = "id", source = "usuario.id")
    @Mapping(target = "nome", source = "usuario.nome")
    @Mapping(target = "email", source = "usuario.email")
    @Mapping(target = "permissoes", source = "usuario.permissoes")
    LoginResponse toLoginResponse(Usuario usuario, String token, Long expiresIn);
}
