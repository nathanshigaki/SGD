package com.govmt.sgd.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.govmt.sgd.model.Documento;

public interface DocumentoRepository extends JpaRepository<Documento, UUID> {

}
