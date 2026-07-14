package com.govmt.sgd.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    private final UsuarioService usuarioService;
    private final HistoricoService historicoService;
    private final DocumentoRepository documentoRepository;
    private final DocumentoMapper documentoMapper;

    @Transactional
    public DocumentoResponse createDocumento(DocumentoRequest request) {
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
    public Page<DocumentoResponse> getAll(Pageable pageable) {
        return documentoRepository.findAllWithResponsaveis(pageable)
                .map(documentoMapper::toResponseFromDocumento);
    }

    @Transactional(readOnly = true)
    public Page<DocumentoResponse> buscarComFiltros( 
            String sigdoc, 
            String situacao, 
            LocalDateTime chegouEm, 
            Boolean condes, 
            String parecerFinal, 
            Pageable pageable) {
        return documentoRepository.buscarComFiltros(sigdoc, situacao, chegouEm, condes, parecerFinal, pageable)
                .map(documentoMapper::toResponseFromDocumento);
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

        return estadoDepois;
    } 
    //Qual seria o requisito para aprovar admin? cargo?
    //Cargo seria apenas um titulo ou seria pre-definições?  
    //update(caso n for admin -> criar uma solicitação) -> aprovar ou nn a solicitação 
    //updade (caso seja -> admin) -> atualiza 

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
            estadoDepois 
        );
    }
}
