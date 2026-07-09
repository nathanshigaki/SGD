package com.govmt.sgd.service;

import com.govmt.sgd.mappers.OrgaoMapper;
import com.govmt.sgd.model.Orgao;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.govmt.sgd.dto.request.OrgaoRequest;
import com.govmt.sgd.dto.response.OrgaoResponse;
import com.govmt.sgd.exception.NotFoundException;
import com.govmt.sgd.repository.OrgaoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrgaoService {

    private final OrgaoMapper orgaoMapper;
    private final OrgaoRepository orgaoRepository;
    private final UsuarioService usuarioService;
    private final HistoricoService historicoService;

    @Transactional
    public OrgaoResponse createOrgao(OrgaoRequest orgaoRequest){
        OrgaoResponse estadoDepois = orgaoMapper.toResponseFromOrgao(orgaoRepository.save(orgaoMapper.toOrgaoFromRequest(orgaoRequest)));

        historicoService.saveHistorico(
            null, 
            usuarioService.getUsuarioLogado(), 
            "CRIAR_ORGAO", 
            null,           
            estadoDepois
        );
        
        return estadoDepois;
    }

    @Transactional(readOnly = true)
    public Page<OrgaoResponse> getAll(Pageable pageble){
        return orgaoRepository.findAll(pageble)
                .map(orgaoMapper::toResponseFromOrgao);
    }

    @Transactional(readOnly = true)
    public OrgaoResponse findById(UUID id){
        return orgaoRepository.findById(id)
                .map(orgaoMapper::toResponseFromOrgao)
                .orElseThrow(() -> new NotFoundException("Órgão não encontrado"));
    }

    @Transactional
    public OrgaoResponse updateOrgao(OrgaoRequest orgaoRequest){
        Orgao orgao = orgaoRepository.findById(orgaoRequest.id())
                .orElseThrow(() -> new NotFoundException("Órgão não encontrado"));

        OrgaoResponse estadoAntes = orgaoMapper.toResponseFromOrgao(orgao);
        orgaoMapper.updateOrgaoFromRequest(orgaoRequest, orgao);
        OrgaoResponse estadoDepois = orgaoMapper.toResponseFromOrgao(orgao);

        historicoService.saveHistorico(
            null, 
            usuarioService.getUsuarioLogado(), 
            "ATUALIZAR_ORGAO", 
            estadoAntes,           
            estadoDepois
        );

        return estadoDepois;
    }

    @Transactional
    public void deleteOrgao(UUID id){
        Orgao orgaoExiste = orgaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Órgão não encontrado"));
        orgaoRepository.delete(orgaoExiste);

        OrgaoResponse estadoAntes = orgaoMapper.toResponseFromOrgao(orgaoExiste);
        orgaoExiste.setDeletadoEm(LocalDateTime.now()); //softdelete
        OrgaoResponse estadoDepois = orgaoMapper.toResponseFromOrgao(orgaoExiste);
        historicoService.saveHistorico(
            null, 
            usuarioService.getUsuarioLogado(), 
            "ATUALIZAR_DOCUMENTO", 
            estadoAntes, 
            estadoDepois 
        );
    }
}
