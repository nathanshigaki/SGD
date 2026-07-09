package com.govmt.sgd.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.govmt.sgd.model.Feriado;

public interface FeriadoRepository extends JpaRepository<Feriado, UUID>{

}
