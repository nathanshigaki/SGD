package com.govmt.sgd.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.govmt.sgd.model.Historico;

public interface HistoricoRepository extends JpaRepository<Historico, UUID> {

	@Query(value = "SELECT h FROM Historico h LEFT JOIN FETCH h.documento LEFT JOIN FETCH h.usuario LEFT JOIN FETCH h.aprovador",
    		countQuery = "SELECT count(h) FROM Historico h")
    Page<Historico> getAll(Pageable pageable);

    @Query(
		value = """
            SELECT h FROM Historico h 
            LEFT JOIN FETCH h.documento 
            LEFT JOIN FETCH h.usuario 
            LEFT JOIN FETCH h.aprovador 
            WHERE (cast(:documentoId as org.hibernate.type.UUIDCharType) IS NULL OR h.documento.id = :documentoId)
            AND (cast(:usuarioId as org.hibernate.type.UUIDCharType) IS NULL OR h.usuario.id = :usuarioId)
            AND (cast(:aprovadorId as org.hibernate.type.UUIDCharType) IS NULL OR h.aprovador.id = :aprovadorId)
            AND (:situacao IS NULL OR h.situacao = :situacao)
            AND (cast(:dataInicio as timestamp) IS NULL OR h.criadoEm >= :dataInicio)
            AND (cast(:dataFim as timestamp) IS NULL OR h.criadoEm <= :dataFim)
            """,
        countQuery = """
            SELECT count(h) FROM Historico h 
            WHERE (cast(:documentoId as org.hibernate.type.UUIDCharType) IS NULL OR h.documento.id = :documentoId)
            AND (cast(:usuarioId as org.hibernate.type.UUIDCharType) IS NULL OR h.usuario.id = :usuarioId)
            AND (cast(:aprovadorId as org.hibernate.type.UUIDCharType) IS NULL OR h.aprovador.id = :aprovadorId)
            AND (:situacao IS NULL OR h.situacao = :situacao)
            AND (cast(:dataInicio as timestamp) IS NULL OR h.criadoEm >= :dataInicio)
            AND (cast(:dataFim as timestamp) IS NULL OR h.criadoEm <= :dataFim)
    """)
    Page<Historico> buscarComFiltros(
		@Param("documentoId") UUID documentoId,
		@Param("usuarioId") UUID usuarioId,
		@Param("aprovadorId") UUID aprovadorId,
		@Param("situacao") String situacao,
		@Param("dataInicio") LocalDateTime dataInicio,
		@Param("dataFim") LocalDateTime dataFim,
		Pageable pageable
	);
}
