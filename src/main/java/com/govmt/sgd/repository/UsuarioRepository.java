package com.govmt.sgd.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import com.govmt.sgd.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

}
