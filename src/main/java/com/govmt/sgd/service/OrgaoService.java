package com.govmt.sgd.service;

import com.govmt.sgd.mappers.OrgaoMapper;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.govmt.sgd.dto.request.OrgaoRequest;
import com.govmt.sgd.dto.response.OrgaoResponse;
import com.govmt.sgd.repository.OrgaoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrgaoService {

    private final OrgaoMapper orgaoMapper;
    private final OrgaoRepository orgaoRepository;

    @Transactional
    public OrgaoResponse createOrgao(OrgaoRequest orgaoRequest){
        if(orgaoRequest.nome() == null) throw new IllegalArgumentException("sem nome");

        return orgaoMapper.toResponseFromOrgao(orgaoRepository.save(orgaoMapper.toOrgaoFromRequest(orgaoRequest)));
    }

    @Transactional(readOnly = true)
    public List<OrgaoResponse> getAll(){
        return orgaoRepository.findAll().stream().map(orgaoMapper::toResponseFromOrgao).toList();
    }

    @Transactional(readOnly = true)
    public OrgaoResponse findById(UUID id){
        return orgaoRepository.findById(id).map(orgaoMapper::toResponseFromOrgao)
                .orElseThrow(() -> new RuntimeException("n existe"));
    }

    @Transactional
    public void OrgaoResponse(UUID id){
        OrgaoResponse orgaoExiste = findById(id);
        orgaoRepository.delete(orgaoMapper.toOrgaoFromResponse(orgaoExiste));
    }
}
