package com.govmt.sgd.repository;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.govmt.sgd.model.Documento;

public interface DocumentoRepository extends JpaRepository<Documento, UUID> {

    @Query("SELECT d FROM Documento d LEFT JOIN FETCH d.orgao LEFT JOIN FETCH d.usuarios du LEFT JOIN FETCH du.usuario")
    Page<Documento> findAllWithResponsaveis(Pageable pageable);

    @Query(value = """
            SELECT d FROM Documento d 
            LEFT JOIN FETCH d.orgao 
            WHERE (:sigdoc IS NULL OR LOWER(d.sigdoc) LIKE LOWER(CONCAT('%', :sigdoc, '%')))
            AND (:situacao IS NULL OR d.situacao = :situacao)
            AND (:chegouEm IS NULL OR d.chegouEm = :chegouEm)
            AND (:condes IS NULL OR d.condes = :condes)
            AND (:parecerFinal IS NULL OR d.parecerFinal = :parecerFinal)
            """,
           countQuery = """
            SELECT count(d) FROM Documento d 
            WHERE (:sigdoc IS NULL OR LOWER(d.sigdoc) LIKE LOWER(CONCAT('%', :sigdoc, '%')))
            AND (:situacao IS NULL OR d.situacao = :situacao)
            AND (:chegouEm IS NULL OR d.chegouEm = :chegouEm)
            AND (:condes IS NULL OR d.condes = :condes)
            AND (:parecerFinal IS NULL OR d.parecerFinal = :parecerFinal)
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
