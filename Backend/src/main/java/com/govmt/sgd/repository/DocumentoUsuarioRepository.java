package com.govmt.sgd.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.govmt.sgd.model.DocumentoUsuario;

public interface DocumentoUsuarioRepository extends JpaRepository<DocumentoUsuario, UUID> {

    

    @Query(
        value = """
        SELECT du FROM DocumentoUsuario du 
        LEFT JOIN FETCH du.documento
        LEFT JOIN FETCH du.usuario 
        WHERE (cast(:documentoId as org.hibernate.type.UUIDCharType) IS NULL OR du.documento.id = :documentoId)
        AND (cast(:usuarioId as org.hibernate.type.UUIDCharType) IS NULL OR du.usuario.id = :usuarioId)
        AND (:cargo IS NULL OR LOWER(du.cargo) LIKE LOWER(CONCAT('%', :cargo, '%')))
        """,
        countQuery = """
        SELECT count(du) FROM DocumentoUsuario du 
        WHERE (cast(:documentoId as org.hibernate.type.UUIDCharType) IS NULL OR du.documento.id = :documentoId)
        AND (cast(:usuarioId as org.hibernate.type.UUIDCharType) IS NULL OR du.usuario.id = :usuarioId)
        AND (:cargo IS NULL OR LOWER(du.cargo) LIKE LOWER(CONCAT('%', :cargo, '%')))
        """)
    Page<DocumentoUsuario> buscarComFiltros(
        @Param("documentoId") UUID documentoId,
        @Param("usuarioId") UUID usuarioId,
        @Param("cargo") String cargo,
        Pageable pageable
    );
}
