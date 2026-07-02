package com.govmt.sgd.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.govmt.sgd.model.Documento;

public interface DocumentoRepository extends JpaRepository<Documento, UUID> {

    @Query("SELECT d FROM Documento d LEFT JOIN FETCH d.orgao LEFT JOIN FETCH d.usuarios du LEFT JOIN FETCH du.usuario")
    List<Documento> findAllWithResponsaveis();
}
