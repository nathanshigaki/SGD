package com.govmt.sgd.repository.specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.govmt.sgd.model.Historico;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class HistoricoSpecs {
    
    public static Specification<Historico> comFiltros(
            UUID documentoId, UUID usuarioId, UUID aprovadorId, 
            String situacao, LocalDateTime dataInicio, LocalDateTime dataFim) {
        
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Evita o problema de N+1 queries trazendo as tabelas relacionadas
            if (Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("documento", JoinType.LEFT);
                root.fetch("usuario", JoinType.LEFT);
                root.fetch("aprovador", JoinType.LEFT);
            }

            if (documentoId != null) {
                predicates.add(cb.equal(root.get("documento").get("id"), documentoId));
            }

            if (usuarioId != null) {
                predicates.add(cb.equal(root.get("usuario").get("id"), usuarioId));
            }

            if (aprovadorId != null) {
                predicates.add(cb.equal(root.get("aprovador").get("id"), aprovadorId));
            }

            if (situacao != null && !situacao.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("situacao"), situacao));
            }

            if (dataInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("criadoEm"), dataInicio));
            }
            if (dataFim != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("criadoEm"), dataFim));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
