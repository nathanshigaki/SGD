package com.govmt.sgd.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.govmt.sgd.dto.request.FeriadoRequest;
import com.govmt.sgd.dto.response.FeriadoResponse;
import com.govmt.sgd.model.Feriado;

@Mapper(componentModel = "spring")
public interface FeriadoMapper {
    
    @Mapping(target = "criadoEm", ignore = true)
    Feriado toFeriadoFromRequest(FeriadoRequest request);
    FeriadoRequest toRequestFromFeriado(Feriado feriado);
    
    Feriado toFeriadoFromResponse(FeriadoResponse response);
    FeriadoResponse toResponseFromFeriado(Feriado feriado);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "criadoEm", ignore = true)
    void updateFeriadoFromRequest(FeriadoRequest request, @MappingTarget Feriado entity);
}
