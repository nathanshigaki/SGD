package com.govmt.sgd.mappers;

import com.govmt.sgd.dto.response.DocumentoUsuarioResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.govmt.sgd.dto.request.DocumentoUsuarioRequest;
import com.govmt.sgd.model.DocumentoUsuario;

@Mapper(componentModel = "spring", uses = {DocumentoMapper.class, UsuarioMapper.class})
public interface DocumentoUsuarioMapper {

    @Mapping(source = "documentoId", target = "documento.id")
    @Mapping(source = "usuarioId", target = "usuario.id")
    DocumentoUsuario toDocumentoUsuarioFromRequest(DocumentoUsuarioRequest request);

    @Mapping(source = "documento.id", target = "documentoId")
    @Mapping(source = "usuario.id", target = "usuarioId")
    DocumentoUsuarioRequest toRequestFromDocumentoUsuario(DocumentoUsuario documentoUsuario);

    DocumentoUsuarioResponse toResponseFromDocumentoUsuario(DocumentoUsuario documentoUsuario);
    DocumentoUsuario toDocumentoUsuarioFromResponse(DocumentoUsuarioResponse response);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "documentoId", target = "documento.id")
    @Mapping(source = "usuarioId", target = "usuario.id")
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    void updateDocumentoUsuarioFromRequest(DocumentoUsuarioRequest request, @MappingTarget DocumentoUsuario entity);
}
