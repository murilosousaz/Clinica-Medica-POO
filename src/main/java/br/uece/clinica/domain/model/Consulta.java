package br.uece.clinica.domain.model;

import br.uece.clinica.domain.valueobject.Diagnostico;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consulta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Consulta extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(name = "data_consulta", nullable = false)
    private LocalDate dataConsulta;

    @Column(name = "horario", length = 10)
    private String horario;

    @Column(name = "observacoes", columnDefinition = "TEXT")
    private String observacoes;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "sintomas", column = @Column(name = "diag_sintomas")),
            @AttributeOverride(name = "diagnosticoTexto", column = @Column(name = "diag_texto")),
            @AttributeOverride(name = "tratamentoSugerido", column = @Column(name = "diag_tratamento")),
            @AttributeOverride(name = "observacoes", column = @Column(name = "diag_observacoes"))
    })
    private Diagnostico diagnostico;

    @Column(name = "receita", columnDefinition = "TEXT")
    private String receita;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "consulta_exames", joinColumns = @JoinColumn(name = "consulta_id"))
    @Column(name = "exame")
    private List<String> examesSolicitados = new ArrayList<>();

    @Column(name = "valor_pago", nullable = false)
    private BigDecimal valorPago = BigDecimal.ZERO;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StatusConsulta status = StatusConsulta.AGENDADA;

    @OneToOne(mappedBy = "consulta", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Avaliacao avaliacao;

    public enum StatusConsulta {
        AGENDADA, REALIZADA, CANCELADA, FALTOU
    }

    public Consulta(Medico medico, Paciente paciente, LocalDate dataConsulta, String horario) {
        this.medico = medico;
        this.paciente = paciente;
        this.dataConsulta = dataConsulta;
        this.horario = horario;
        this.status = StatusConsulta.AGENDADA;
    }

    public Consulta(Paciente paciente, Medico medico, LocalDateTime dataHora, String observacoes) {
        this.medico = medico;
        this.paciente = paciente;
        atualizarDataHora(dataHora);
        this.observacoes = observacoes;
        this.status = StatusConsulta.AGENDADA;
    }

    public void atualizarDataHora(LocalDateTime dataHora) {
        if (dataHora == null) {
            throw new IllegalArgumentException("Data e hora da consulta são obrigatórias");
        }
        this.dataConsulta = dataHora.toLocalDate();
        this.horario = dataHora.toLocalTime().toString();
    }

    public LocalDateTime getDataHora() {
        LocalTime hora = horario != null ? LocalTime.parse(horario) : LocalTime.MIDNIGHT;
        return LocalDateTime.of(dataConsulta, hora);
    }

    public void realizarConsulta(Diagnostico diagnostico, String receita, List<String> exames) {
        if (status != StatusConsulta.AGENDADA) {
            throw new IllegalStateException("Consulta não está agendada");
        }
        this.diagnostico = diagnostico;
        this.receita = receita;
        this.examesSolicitados = exames;
        this.status = StatusConsulta.REALIZADA;
        calcularValor();
    }

    private void calcularValor() {
        String nomePlano = paciente.getPlanoSaude() != null ?
                paciente.getPlanoSaude().getNome() : null;
        this.valorPago = medico.getValorConsulta(paciente.temPlanoSaude(), nomePlano);
    }

    public void cancelar() {
        this.status = StatusConsulta.CANCELADA;
    }

    public void marcarFalta() {
        this.status = StatusConsulta.FALTOU;
    }

    public boolean podeSerAvaliada() {
        return status == StatusConsulta.REALIZADA && avaliacao == null;
    }
}