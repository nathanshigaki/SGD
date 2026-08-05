package com.govmt.sgd.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import com.govmt.sgd.model.Historico;

public interface HistoricoRepository extends JpaRepository<Historico, UUID>, JpaSpecificationExecutor<Historico> {

	@Query(
    value = """
            SELECT h FROM Historico h 
            LEFT JOIN FETCH h.documento 
            LEFT JOIN FETCH h.usuario 
            LEFT JOIN FETCH h.aprovador 
            WHERE h.situacao != 'PENDENTE_APROVACAO'
    """, //N APARECE OS PENDENTES NO HISTORICO COMUM
    		countQuery = "SELECT count(h) FROM Historico h WHERE h.situacao != 'PENDENTE_APROVACAO'")
  	Page<Historico> getAll(Pageable pageable);

}
