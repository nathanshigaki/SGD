package com.govmt.sgd.service;

import com.govmt.sgd.mappers.OrgaoMapper;
import com.govmt.sgd.model.Orgao;

import java.util.List;
import java.util.UUID;

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

    @Transactional
    public OrgaoResponse createOrgao(OrgaoRequest orgaoRequest){
        return orgaoMapper.toResponseFromOrgao(orgaoRepository.save(orgaoMapper.toOrgaoFromRequest(orgaoRequest)));
    }

    @Transactional(readOnly = true)
    public List<OrgaoResponse> getAll(){
        return orgaoRepository.findAll()
                .stream()
                .map(orgaoMapper::toResponseFromOrgao)
                .toList();
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

        orgaoMapper.updateOrgaoFromRequest(orgaoRequest, orgao);
        return orgaoMapper.toResponseFromOrgao(orgao);
    }

    @Transactional
    public void deleteOrgao(UUID id){
        Orgao orgaoExiste = orgaoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Órgão não encontrado"));
        orgaoRepository.delete(orgaoExiste);
    }
}
