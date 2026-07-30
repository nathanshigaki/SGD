package com.govmt.sgd.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "documentos")
@SQLRestriction("deletado_em IS NULL")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orgao_id", nullable = false)
    private Orgao orgao;  

    @OneToMany(mappedBy = "documento")
    List<DocumentoUsuario> usuarios;

    @Column(nullable = false)
    private String sigdoc; 

    @Column(name = "chegou_em")
    private LocalDateTime chegouEm;  

    @Column(name = "concluiu_em")
    private LocalDateTime concluiuEm;   

    @Column(name = "em_espera")
    private int emEspera;

    @Column(precision = 15, scale = 2)
    private BigDecimal valor;  

    @Column(name = "situacao")
    private String situacao;
    
    @Column(name = "caracterizacao_ti")
    private String caracterizacaoTi;   

    @Column(name = "iniciado")
    private Boolean iniciado;  

    @Column(name = "condes")
    private Boolean condes;
    
    @Column(name = "resumo")
    private String resumo; 
    
    @Column(name = "tipo_contratacao")
    private String tipoContratacao;   

    @Column(name = "objeto")
    private String objeto;

    @Column(name = "recomendacao")
    private String recomendacao;

    @Column(name = "parecer_final")
    private String parecerFinal;

    @Column(name = "deletado_em")
    private LocalDateTime deletadoEm;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}
