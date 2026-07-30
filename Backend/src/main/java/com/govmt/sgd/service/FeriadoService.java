package com.govmt.sgd.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.govmt.sgd.dto.request.FeriadoRequest;
import com.govmt.sgd.dto.response.FeriadoResponse;
import com.govmt.sgd.exception.NotFoundException;
import com.govmt.sgd.mappers.FeriadoMapper;
import com.govmt.sgd.model.Feriado;
import com.govmt.sgd.repository.FeriadoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeriadoService {

    private final FeriadoRepository feriadoRepository;
    private final FeriadoMapper feriadoMapper;


    @Transactional
    public FeriadoResponse createFeriado(FeriadoRequest request) {
        return feriadoMapper.toResponseFromFeriado(feriadoRepository.save(feriadoMapper.toFeriadoFromRequest(request)));
    }

    @Transactional(readOnly = true)
    public Page<FeriadoResponse> getAll(Pageable pageable) {
        return feriadoRepository.findAll(pageable)
                .map(feriadoMapper::toResponseFromFeriado);
    }

    @Transactional(readOnly = true)
    public FeriadoResponse findById(UUID id) {
        return feriadoRepository.findById(id)
                .map(feriadoMapper::toResponseFromFeriado)
                .orElseThrow(() -> new NotFoundException("Feriado não encontrado"));
    }

    @Transactional
    public FeriadoResponse updateFeriado(FeriadoRequest request) {
        Feriado feriado = feriadoRepository.findById(request.id())
                .orElseThrow(() -> new NotFoundException("Feriado não encontrado"));
                
        feriadoMapper.updateFeriadoFromRequest(request, feriado);

        return feriadoMapper.toResponseFromFeriado(feriado);
    }

    @Transactional
    public void deleteFeriado(UUID id) {
        Feriado feriadoExiste = feriadoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Feriado não encontrado"));
        
        feriadoRepository.delete(feriadoExiste);
    }
}
