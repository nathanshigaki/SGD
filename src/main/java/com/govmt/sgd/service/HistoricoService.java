package com.govmt.sgd.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.govmt.sgd.dto.ValoresHistorico;
import com.govmt.sgd.dto.response.HistoricoResponse;
import com.govmt.sgd.mappers.HistoricoMapper;
import com.govmt.sgd.model.Documento;
import com.govmt.sgd.model.Historico;
import com.govmt.sgd.model.Usuario;
import com.govmt.sgd.repository.HistoricoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoricoService {

    private final HistoricoRepository historicoRepository;
    private final HistoricoMapper historicoMapper;

    @Transactional
    public void saveHistorico(Documento documento, Usuario usuario, Usuario aprovador, String situacao, String acao, Object antes, Object depois) {
        Historico historico = new Historico();
        historico.setDocumento(documento);
        historico.setUsuario(usuario);
        historico.setAprovador(null);
        //adicionar no documentoService o metodo para pegar o id do jwt q vai ser o aprovador, 
        //adicionar a condição do aprovador para aprovar as mudanças e assim mudar as coisas, enquanto n tiver deixar pre salvo para o aprovador aprovar.
        historico.setSituacao(situacao);
        historico.setAcao(acao);
        historico.setValores(new ValoresHistorico(antes, depois));
        
        historicoRepository.save(historico);
    }

    @Transactional
    public HistoricoResponse solicitarAprovacao(Documento documento, Usuario usuario, String acao, Object antes, Object depois){
        Historico historico = new Historico();
        historico.setDocumento(documento);
        historico.setUsuario(usuario);
        historico.setAprovador(null);
        historico.setSituacao("PENDENTE_APROVACAO"); 
        historico.setAcao(acao);
        historico.setValores(new ValoresHistorico(antes, depois));
        //gambiarra salvando no historico, na paginação do historico iria ignorar "PENDENTE_APROVACAO"
        //criaria outra paginação para mostrar as solitações pendentes 
        //mlr solução seria fazer um cache?
        return historicoMapper.toResponseFromHistorico(historicoRepository.save(historico));
    }

    @Transactional(readOnly = true)
    public Page<HistoricoResponse> getAll(Pageable pageable) {
        return historicoRepository.getAll(pageable)
                .map(historicoMapper::toResponseFromHistorico);
    }

    @Transactional(readOnly = true)
    public Page<HistoricoResponse> buscarHistoricoComFiltros(
            UUID documentoId, 
            UUID usuarioId, 
            UUID aprovadorId, 
            String situacao, 
            LocalDateTime dataInicio, 
            LocalDateTime dataFim, 
            Pageable pageable) {

        return historicoRepository.buscarComFiltros(
                documentoId, usuarioId, aprovadorId, situacao, dataInicio, dataFim, pageable)
                .map(historicoMapper::toResponseFromHistorico);
    }
}
