package com.govmt.sgd.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.govmt.sgd.model.DocumentoUsuario;

public interface DocumentoUsuarioRepository extends JpaRepository<DocumentoUsuario, UUID> {

    //filtrar por cargo
    //filtrar por usuario
    //filtrar por documento
}
