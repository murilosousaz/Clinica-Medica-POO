package br.uece.clinica.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "conta")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"paciente", "consulta"})
public class Conta extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consulta_id")
    private Consulta consulta;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SituacaoConta situacao = SituacaoConta.PENDENTE;

    public Conta(Paciente paciente, BigDecimal valor, String descricao, LocalDate dataVencimento) {
        this.paciente = paciente;
        this.valor = valor;
        this.descricao = descricao;
        this.dataVencimento = dataVencimento;
        this.situacao = SituacaoConta.PENDENTE;
    }

    public Conta(Paciente paciente, Consulta consulta, BigDecimal valor,
                 String descricao, LocalDate dataVencimento) {
        this(paciente, valor, descricao, dataVencimento);
        this.consulta = consulta;
    }

    public void pagar() {
        this.situacao = SituacaoConta.PAGO;
        this.dataPagamento = LocalDate.now();
    }

    public void verificarVencimento() {
        if (situacao == SituacaoConta.PENDENTE && dataVencimento.isBefore(LocalDate.now())) {
            this.situacao = SituacaoConta.VENCIDA;
        }
    }

    public boolean estaPendente() {
        return situacao == SituacaoConta.PENDENTE;
    }

    public boolean estaVencida() {
        return situacao == SituacaoConta.VENCIDA;
    }

    public boolean estaPago() {
        return situacao == SituacaoConta.PAGO;
    }

    public enum SituacaoConta {
        PENDENTE, PAGO, VENCIDA
    }
}
