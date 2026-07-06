package com.govmt.sgd.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.govmt.sgd.model.Orgao;

public interface OrgaoRepository extends JpaRepository<Orgao, UUID> {

    //filtrar por nome
}
