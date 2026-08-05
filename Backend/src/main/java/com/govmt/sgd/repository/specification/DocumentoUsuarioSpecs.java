package com.govmt.sgd.repository.specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.govmt.sgd.model.DocumentoUsuario;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class DocumentoUsuarioSpecs {

    public static Specification<DocumentoUsuario> comFiltros(
            UUID documentoId, UUID usuarioId, String cargo) {
        
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("documento", JoinType.LEFT);
                root.fetch("usuario", JoinType.LEFT);
            }

            if (documentoId != null) {
                predicates.add(cb.equal(root.get("documento").get("id"), documentoId));
            }

            if (usuarioId != null) {
                predicates.add(cb.equal(root.get("usuario").get("id"), usuarioId));
            }

            if (cargo != null && !cargo.trim().isEmpty()) {
                predicates.add(cb.like(cb.upper(root.get("cargo")), "%" + cargo.toUpperCase() + "%"));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
