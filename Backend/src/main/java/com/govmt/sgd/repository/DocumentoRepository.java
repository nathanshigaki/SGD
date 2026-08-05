package com.govmt.sgd.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.antlr.v4.runtime.atn.SemanticContext.OR;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.govmt.sgd.model.Documento;

public interface DocumentoRepository extends JpaRepository<Documento, UUID> {

    boolean existsByOrgaoId(UUID orgaoId);

    @Query("SELECT d FROM Documento d LEFT JOIN FETCH d.orgao LEFT JOIN FETCH d.usuarios du LEFT JOIN FETCH du.usuario")
    Page<Documento> findAllWithResponsaveis(Pageable pageable);

    @Query(
        value = """
        SELECT d FROM Documento d 
        LEFT JOIN FETCH d.orgao 
        WHERE (CAST(:sigdoc AS String) IS NULL OR UPPER(d.sigdoc) LIKE UPPER(CONCAT('%', CAST(:sigdoc AS String), '%')))
        AND (CAST(:situacao AS String) IS NULL OR d.situacao = CAST(:situacao AS String))
        AND (CAST(:chegouEm AS LocalDateTime) IS NULL OR d.chegouEm = CAST(:chegouEm AS LocalDateTime))
        AND (CAST(:condes AS Boolean) IS NULL OR d.condes = CAST(:condes AS Boolean))
        AND (CAST(:parecerFinal AS String) IS NULL OR d.parecerFinal = CAST(:parecerFinal AS String))
        """,
        countQuery = """
        SELECT count(d) FROM Documento d 
        WHERE (CAST(:sigdoc AS String) IS NULL OR UPPER(d.sigdoc) LIKE UPPER(CONCAT('%', CAST(:sigdoc AS String), '%')))
        AND (CAST(:situacao AS String) IS NULL OR d.situacao = CAST(:situacao AS String))
        AND (CAST(:chegouEm AS LocalDateTime) IS NULL OR d.chegouEm = CAST(:chegouEm AS LocalDateTime))
        AND (CAST(:condes AS Boolean) IS NULL OR d.condes = CAST(:condes AS Boolean))
        AND (CAST(:parecerFinal AS String) IS NULL OR d.parecerFinal = CAST(:parecerFinal AS String))
        """)
    Page<Documento> buscarComFiltros(
        @Param("sigdoc") String sigdoc,
        @Param("situacao") String situacao,
        @Param("chegouEm") LocalDateTime chegouEm,
        @Param("condes") Boolean condes,
        @Param("parecerFinal") String parecerFinal,
        Pageable pageable
    );

    //JpaSpecificationExecutor para buscar por múltiplos critérios, caso seja necessário no futuro
}
