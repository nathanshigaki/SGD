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
import com.govmt.sgd.model.Usuario;
import com.govmt.sgd.repository.DocumentoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentoService {

    private final OrgaoService orgaoService;
    private final UsuarioService usuarioService;
    private final HistoricoService historicoService;
    private final DocumentoRepository documentoRepository;
    private final DocumentoMapper documentoMapper;

    @Transactional
    public DocumentoResponse createDocumento(DocumentoRequest request) {
        orgaoService.findById(request.orgaoId());

        Documento documento = documentoRepository.save(documentoMapper.toDocumentoFromRequest(request));
        DocumentoResponse estadoDepois = documentoMapper.toResponseFromDocumento(documento);

        historicoService.saveHistorico(
            documento, 
            usuarioService.getUsuarioLogado(), 
            "CRIAR_DOCUMENTO", 
            null,           
            estadoDepois  
        );
        return estadoDepois;
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

        DocumentoResponse estadoAntes = documentoMapper.toResponseFromDocumento(documento);
        documentoMapper.updateDocumentoFromRequest(request, documento);
        DocumentoResponse estadoDepois = documentoMapper.toResponseFromDocumento(documento);

        historicoService.saveHistorico(
            documento, 
            usuarioService.getUsuarioLogado(), 
            "ATUALIZAR_DOCUMENTO", 
            estadoAntes, 
            estadoDepois
        );

        return documentoMapper.toResponseFromDocumento(documento);
    }

    @Transactional
    public void deleteDocumento(UUID id) {
        Documento documento = documentoRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Documento não encontrado"));

        DocumentoResponse estadoAntes = documentoMapper.toResponseFromDocumento(documento);
        documento.setDeletadoEm(LocalDateTime.now()); //softdelete
        DocumentoResponse estadoDepois = documentoMapper.toResponseFromDocumento(documento);
        historicoService.saveHistorico(
            documento, 
            usuarioService.getUsuarioLogado(), 
            "ATUALIZAR_DOCUMENTO", 
            estadoAntes, 
            estadoDepois // deletar ou continua igual?
        );
    }
}
