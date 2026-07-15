package com.govmt.sgd.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.govmt.sgd.dto.request.DocumentoRequest;
import com.govmt.sgd.dto.response.DocumentoResponse;
import com.govmt.sgd.exception.InvalidArgumentException;
import com.govmt.sgd.exception.NotFoundException;
import com.govmt.sgd.mappers.DocumentoMapper;
import com.govmt.sgd.model.Documento;
import com.govmt.sgd.model.Historico;
import com.govmt.sgd.model.Usuario;
import com.govmt.sgd.repository.DocumentoRepository;
import com.govmt.sgd.repository.HistoricoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentoService {

    private final UsuarioService usuarioService;
    private final HistoricoService historicoService;
    private final HistoricoRepository historicoRepository;
    private final DocumentoRepository documentoRepository;
    private final DocumentoMapper documentoMapper;
    private final ObjectMapper objectMapper;

    //cud(create, update, delete) solicitarAprovação -> ValidaSolicitação -> cud
    @Transactional
    public DocumentoResponse createDocumento(DocumentoRequest request) {
        Usuario usuario = usuarioService.getUsuarioLogado();
        if (usuario.getPermissoes().contains("*:*")) {
            Documento salvar = documentoRepository.save(documentoMapper.toDocumentoFromRequest(request));
            return documentoMapper.toResponseFromDocumento(salvar);
        }

        Documento documentoProposto = documentoMapper.toDocumentoFromRequest(request);

        historicoService.solicitarAprovacao(
            documentoProposto, 
            usuario, 
            "CRIAR_DOCUMENTO", 
            null,           
            request  
        );
        return documentoMapper.toResponseFromDocumento(documentoProposto);
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

        Usuario usuario = usuarioService.getUsuarioLogado();

        if (usuario.getPermissoes().contains("*:*")) {
            DocumentoResponse estadoAntes = documentoMapper.toResponseFromDocumento(documento);
            documentoMapper.updateDocumentoFromRequest(request, documento);
            Documento documentoSalvo = documentoRepository.save(documento);
            DocumentoResponse estadoDepois = documentoMapper.toResponseFromDocumento(documentoSalvo);
            historicoService.saveHistorico(documentoSalvo, usuario, usuario, "APROVADO", "ATUALIZAR_DOCUMENTO", estadoAntes, estadoDepois);
            return estadoDepois;
        }

        DocumentoResponse estadoAntes = documentoMapper.toResponseFromDocumento(documento);
        historicoService.solicitarAprovacao(
            documento, 
            usuario, 
            "ATUALIZAR_DOCUMENTO", 
            estadoAntes, 
            request
        );
        
        return estadoAntes;
    } 
    //Qual seria o requisito para aprovar admin? cargo?
    //Quais lugares seriam necessarios a aprovação da ação? 
    //Cargo seria apenas um titulo ou seria pre-definições do usuario?  
    //update(caso n for admin -> criar uma solicitação) -> aprovar ou nn a solicitação 
    //updade (caso seja -> admin) -> atualiza 

    @Transactional
    public void deleteDocumento(UUID id) {
        Documento documento = documentoRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Documento não encontrado"));

        Usuario usuario = usuarioService.getUsuarioLogado();

        if (usuario.getPermissoes().contains("*:*")) {
            DocumentoResponse estadoAntes = documentoMapper.toResponseFromDocumento(documento);
            documento.setDeletadoEm(LocalDateTime.now()); // softdelete
            Documento documentoSalvo = documentoRepository.save(documento);
            DocumentoResponse estadoDepois = documentoMapper.toResponseFromDocumento(documentoSalvo);

            historicoService.saveHistorico(documentoSalvo, usuario, usuario, "APROVADO", "DELETAR_DOCUMENTO", estadoAntes, estadoDepois);
            return;
        }

        DocumentoResponse estadoAntes = documentoMapper.toResponseFromDocumento(documento);
        historicoService.solicitarAprovacao(
            documento, 
            usuario, 
            "DELETAR_DOCUMENTO", 
            estadoAntes, 
            null
        );
    }

    @Transactional
    public Documento executarCriacao(DocumentoRequest request) {
        Documento documento = documentoMapper.toDocumentoFromRequest(request);
        return documentoRepository.save(documento);
    }

    @Transactional
    public Documento executarAtualizacao(DocumentoRequest request) {
        Documento documentoExistente = documentoRepository.findById(request.id())
                .orElseThrow(() -> new NotFoundException("Documento não encontrado"));
                
        documentoMapper.updateDocumentoFromRequest(request, documentoExistente);
        return documentoRepository.save(documentoExistente);
    }

    @Transactional
    public Documento executarDelecao(UUID documentoId) {
        Documento documentoExistente = documentoRepository.findById(documentoId)
                .orElseThrow(() -> new NotFoundException("Documento não encontrado"));
                
        documentoExistente.setDeletadoEm(LocalDateTime.now());
        return documentoRepository.save(documentoExistente);
    }

    @Transactional
    public void validarSolicitacao(UUID historicoId, boolean aprovado) {
        Historico solicitacaoPendente = historicoRepository.findById(historicoId)
                .orElseThrow(() -> new NotFoundException("Solicitação de auditoria não encontrada"));

        if (!"PENDENTE_APROVACAO".equals(solicitacaoPendente.getSituacao())) {
            throw new InvalidArgumentException("Esta solicitação já foi processada anteriormente.");
        }

        Usuario aprovador = usuarioService.getUsuarioLogado();
        if (!aprovado) {
            historicoService.saveHistorico(
                solicitacaoPendente.getDocumento(), 
                solicitacaoPendente.getUsuario(),
                aprovador, 
                "REJEITADO", 
                solicitacaoPendente.getAcao(), 
                solicitacaoPendente.getValores().antes(), 
                solicitacaoPendente.getValores().depois()
            );
            return;
        }

        Documento documentoFinal = null;

        switch (solicitacaoPendente.getAcao()) {
            case "CRIAR_DOCUMENTO" -> {
                DocumentoRequest request = objectMapper.convertValue(solicitacaoPendente.getValores().depois(), DocumentoRequest.class);
                documentoFinal = executarCriacao(request);
            }
            
            case "ATUALIZAR_DOCUMENTO" -> {
                DocumentoRequest request = objectMapper.convertValue(solicitacaoPendente.getValores().depois(), DocumentoRequest.class);
                documentoFinal = executarAtualizacao(request);
            }
            
            case "DELETAR_DOCUMENTO" -> {
                documentoFinal = executarDelecao(solicitacaoPendente.getDocumento().getId());
            }
            
            default -> throw new InvalidArgumentException("Ação de histórico desconhecida.");
        }

        historicoService.saveHistorico(
            documentoFinal, 
            solicitacaoPendente.getUsuario(), 
            aprovador, 
            "APROVADO", 
            solicitacaoPendente.getAcao(), 
            solicitacaoPendente.getValores().antes(), 
            solicitacaoPendente.getValores().depois()
        );
    }
}
