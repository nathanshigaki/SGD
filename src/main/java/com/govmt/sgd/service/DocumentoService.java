package com.govmt.sgd.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.govmt.sgd.dto.request.DocumentoRequest;
import com.govmt.sgd.dto.response.DocumentoResponse;
import com.govmt.sgd.exception.NotFoundException;
import com.govmt.sgd.mappers.DocumentoMapper;
import com.govmt.sgd.model.Documento;
import com.govmt.sgd.repository.DocumentoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentoService {

    private final OrgaoService orgaoService;
    private final DocumentoRepository documentoRepository;
    private final DocumentoMapper documentoMapper;

    @Transactional
    public DocumentoResponse createDocumento(DocumentoRequest request) {
        orgaoService.findById(request.orgaoId());
        return documentoMapper.toResponseFromDocumento(documentoRepository.save(documentoMapper.toDocumentoFromRequest(request)));
    }

    @Transactional(readOnly = true)
    public List<DocumentoResponse> getAll() {
        return documentoRepository.findAllWithResponsaveis()
                .stream()
                .map(documentoMapper::toResponseFromDocumento)
                .toList();
    }

    @Transactional(readOnly = true)
    public DocumentoResponse findById(UUID id) {
        return documentoRepository.findById(id)
                .map(documentoMapper::toResponseFromDocumento)
                .orElseThrow(() -> new NotFoundException("Documento não encontrado"));
    }

    @Transactional
    public DocumentoResponse updateDocumento(DocumentoRequest request) {
        Documento documento = documentoRepository.findById(request.id())
            .orElseThrow(() -> new NotFoundException("Documento não encontrado"));

        documentoMapper.updateDocumentoFromRequest(request, documento);
        return documentoMapper.toResponseFromDocumento(documento);
    }

    @Transactional
    public void deleteDocumento(UUID id) {
        Documento documento = documentoRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Documento não encontrado"));
        documento.setDeletadoEm(LocalDateTime.now());
    }
}
