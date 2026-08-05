package com.govmt.sgd.repository.specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.govmt.sgd.model.Documento;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class DocumentoSpecs {

    public static Specification<Documento> comFiltros(
            String sigdoc, String situacao, LocalDateTime chegouEm, Boolean condes, String parecerFinal) {
        
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Evita erro de N+1 trazendo o Órgão junto (equivalente ao LEFT JOIN FETCH)
            // A checagem de Long.class evita que o Hibernate quebre na hora de fazer o count() da paginação
            if (Long.class != query.getResultType() && long.class != query.getResultType()) {
                root.fetch("orgao", JoinType.LEFT);
            }

            // Filtro: Apenas documentos não deletados
            predicates.add(cb.isNull(root.get("deletadoEm")));

            if (sigdoc != null && !sigdoc.trim().isEmpty()) {
                predicates.add(cb.like(cb.upper(root.get("sigdoc")), "%" + sigdoc.toUpperCase() + "%"));
            }

            if (situacao != null && !situacao.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("situacao"), situacao));
            }

            if (chegouEm != null) {
                predicates.add(cb.equal(root.get("chegouEm"), chegouEm));
            }

            if (condes != null) {
                predicates.add(cb.equal(root.get("condes"), condes));
            }

            if (parecerFinal != null && !parecerFinal.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("parecerFinal"), parecerFinal));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
