package com.govmt.sgd.service;

import java.util.List;
import java.util.UUID;

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

    private final DocumentoUsuarioRepository documentoUsuarioRepository;
    private final DocumentoUsuarioMapper documentoUsuarioMapper;

    @Transactional
    public DocumentoUsuarioResponse createDocumentoUsuario(DocumentoUsuarioRequest request) {
        return documentoUsuarioMapper.toResponseFromDocumentoUsuario(documentoUsuarioRepository.save(documentoUsuarioMapper.toDocumentoUsuarioFromRequest(request)));
    }

    @Transactional(readOnly = true)
    public List<DocumentoUsuarioResponse> getAll() {
        return documentoUsuarioRepository.findAll()
                .stream()
                .map(documentoUsuarioMapper::toResponseFromDocumentoUsuario)
                .toList();
    }

    @Transactional(readOnly = true)
    public DocumentoUsuarioResponse findById(UUID id) {
        return documentoUsuarioRepository.findById(id)
                .map(documentoUsuarioMapper::toResponseFromDocumentoUsuario)
                .orElseThrow(() -> new NotFoundException("Atribuição não encontrada"));
    }

    @Transactional
    public DocumentoUsuarioResponse updateDocumentoUsuario(DocumentoUsuarioRequest request){
        DocumentoUsuarioResponse response = findById(request.id());
        DocumentoUsuario documentoUsuario = documentoUsuarioMapper.toDocumentoUsuarioFromResponse(response);

        documentoUsuarioMapper.updateDocumentoUsuarioFromRequest(request, documentoUsuario);
        return documentoUsuarioMapper.toResponseFromDocumentoUsuario(documentoUsuario);
    }

    @Transactional
    public void deleteDocumentoUsuario(UUID id) {
        DocumentoUsuarioResponse relacao = findById(id);
        documentoUsuarioRepository.delete(documentoUsuarioMapper.toDocumentoUsuarioFromResponse(relacao));
    }
}
