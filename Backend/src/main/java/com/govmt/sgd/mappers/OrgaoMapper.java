package com.govmt.sgd.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.govmt.sgd.dto.request.OrgaoRequest;
import com.govmt.sgd.dto.response.OrgaoResponse;
import com.govmt.sgd.model.Orgao;

@Mapper(componentModel = "spring")
public interface OrgaoMapper {

    @Mapping(target = "deletadoEm", ignore = true)
    Orgao toOrgaoFromRequest(OrgaoRequest request);
    OrgaoRequest toRequestFromOrgao(Orgao orgao);
    
    @Mapping(target = "deletadoEm", ignore = true)
    Orgao toOrgaoFromResponse(OrgaoResponse response);
    OrgaoResponse toResponseFromOrgao(Orgao orgao);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    @Mapping(target = "atualizadoEm", ignore = true)
    @Mapping(target = "deletadoEm", ignore = true)
    void updateOrgaoFromRequest(OrgaoRequest request, @MappingTarget Orgao entity);

}
