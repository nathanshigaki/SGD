package com.govmt.sgd.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.govmt.sgd.dto.request.DocumentoRequest;
import com.govmt.sgd.dto.response.DocumentoResponsavelResponse;
import com.govmt.sgd.dto.response.DocumentoResponse;
import com.govmt.sgd.model.Documento;
import com.govmt.sgd.model.DocumentoUsuario;

@Mapper(componentModel = "spring", uses = {OrgaoMapper.class})
public interface DocumentoMapper {

    @Mapping(source = "orgaoId", target = "orgao.id")
    @Mapping(target = "usuarios", ignore = true)
    Documento toDocumentoFromRequest(DocumentoRequest request);

    @Mapping(source = "orgao.id", target = "orgaoId")
    DocumentoRequest toRequestFromDocumento(Documento documento);
    
    @Mapping(target = "usuarios", ignore = true)
    Documento toDocumentoFromResponse(DocumentoResponse response);

    @Mapping(source = "usuarios", target = "responsaveis")
    DocumentoResponse toResponseFromDocumento(Documento documento);

    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "usuario.nome", target = "nome")
    DocumentoResponsavelResponse toDocumentoResponsavelResponse(DocumentoUsuario documentoUsuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "orgaoId", target = "orgao.id")
    @Mapping(target = "usuarios", ignore = true)
    @Mapping(target = "chegouEm", ignore = true)
    @Mapping(target = "emEspera", ignore = true)
    @Mapping(target = "deletadoEm", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    void updateDocumentoFromRequest(DocumentoRequest request, @MappingTarget Documento entity);
}