package com.govmt.sgd.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.govmt.sgd.dto.request.DocumentoUsuarioRequest;
import com.govmt.sgd.dto.response.DocumentoUsuarioResponse;
import com.govmt.sgd.exception.NotFoundException;
import com.govmt.sgd.mappers.DocumentoUsuarioMapper;
import com.govmt.sgd.model.DocumentoUsuario;
import com.govmt.sgd.repository.DocumentoUsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DocumentoUsuarioService {

    private final UsuarioService usuarioService;
    private final DocumentoService documentoService;
    private final DocumentoUsuarioRepository documentoUsuarioRepository;
    private final DocumentoUsuarioMapper documentoUsuarioMapper;

    @Transactional
    public DocumentoUsuarioResponse createDocumentoUsuario(DocumentoUsuarioRequest request) {
        documentoService.findById(request.documentoId());
        usuarioService.findById(request.usuarioId());

        return documentoUsuarioMapper.toResponseFromDocumentoUsuario(documentoUsuarioRepository.save(documentoUsuarioMapper.toDocumentoUsuarioFromRequest(request)));
    }

    @Transactional(readOnly = true)
    public Page<DocumentoUsuarioResponse> getAll(Pageable pageable) {
        return documentoUsuarioRepository.findAll(pageable)
                .map(documentoUsuarioMapper::toResponseFromDocumentoUsuario);
    }

    @Transactional(readOnly = true)
    public Page<DocumentoUsuarioResponse> buscarComFiltros(
        UUID documentoId,
        UUID usuarioId,
        String cargo,
        Pageable pageable
    ){
        return documentoUsuarioRepository.buscarComFiltros(documentoId, usuarioId, cargo, pageable)
                .map(documentoUsuarioMapper::toResponseFromDocumentoUsuario);
    }

    @Transactional(readOnly = true)
    public DocumentoUsuarioResponse findById(UUID id) {
        return documentoUsuarioRepository.findById(id)
                .map(documentoUsuarioMapper::toResponseFromDocumentoUsuario)
                .orElseThrow(() -> new NotFoundException("Atribuição não encontrada"));
    }

    @Transactional
    public DocumentoUsuarioResponse updateDocumentoUsuario(DocumentoUsuarioRequest request){
        DocumentoUsuario documentoUsuario = documentoUsuarioRepository.findById(request.id())
                .orElseThrow(() -> new NotFoundException("Atribuição não encontrada"));

        documentoUsuarioMapper.updateDocumentoUsuarioFromRequest(request, documentoUsuario);

        return documentoUsuarioMapper.toResponseFromDocumentoUsuario(documentoUsuario);
    }

    @Transactional
    public void deleteDocumentoUsuario(UUID id) {
        DocumentoUsuario documentoUsuario = documentoUsuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Atribuição não encontrada"));
        documentoUsuarioRepository.delete(documentoUsuario);
    }
}
