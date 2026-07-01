package com.govmt.sgd.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "documentos")
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

    @Column(nullable = false)
    private String sigdoc; 

    @Column(name = "chegou_em")
    private Date chegouEm;  

    @Column(name = "concluiu_em")
    private Date concluiuEm;   

    @Column(name = "em_espera")
    private int emEspera;

    @Column(precision = 15, scale = 2)
    private BigDecimal valor;  
    private String situacao;
    
    @Column(name = "caracterizacao_ti")
    private String caracterizacaoTi;   
    private Boolean iniciado;  
    private Boolean condes; 
    private String resumo; 
    
    @Column(name = "tipo_contratacao")
    private String tipoContratacao;   
    private String objeto;
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
