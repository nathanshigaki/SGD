package com.govmt.sgd.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.govmt.sgd.dto.ValoresHistorico;
import com.govmt.sgd.model.Documento;
import com.govmt.sgd.model.Historico;
import com.govmt.sgd.model.Usuario;
import com.govmt.sgd.repository.HistoricoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoricoService {

    private final HistoricoRepository historicoRepository;

    @Transactional
    public void saveHistorico(Documento documento, Usuario usuario, String acao, Object antes, Object depois) {
        Historico historico = new Historico();
        historico.setDocumento(documento);
        historico.setUsuario(usuario);
        historico.setAprovador(null);
        //adicionar no documentoService o metodo para pegar o id do jwt q vai ser o aprovador, 
        //adicionar a condição do aprovador para aprovar as mudanças e assim mudar as coisas, enquanto n tiver deixar pre salvo para o aprovador aprovar.

        historico.setAcao(acao);
        historico.setValores(new ValoresHistorico(antes, depois));
        
        historicoRepository.save(historico);
    }
}
