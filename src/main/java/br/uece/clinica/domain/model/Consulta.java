package br.uece.clinica.domain.model;

import br.uece.clinica.domain.valueobject.Diagnostico;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consulta")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"medico", "paciente", "avaliacao"})
public class Consulta extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @Column(name = "data_consulta", nullable = false)
    private LocalDate dataConsulta;

    private LocalTime horario;

    @Embedded
    private Diagnostico diagnostico;

    @Column(length = 2000)
    private String receita;

    @Column(name = "valor_pago", precision = 10, scale = 2)
    private BigDecimal valorPago;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusConsulta status = StatusConsulta.AGENDADA;

    @ElementCollection
    @CollectionTable(name = "consulta_exames", joinColumns = @JoinColumn(name = "consulta_id"))
    @Column(name = "exame")
    private List<String> exames = new ArrayList<>();

    @OneToOne(mappedBy = "consulta", fetch = FetchType.LAZY)
    private Avaliacao avaliacao;

    public Consulta(Medico medico, Paciente paciente, LocalDate dataConsulta, LocalTime horario) {
        this.medico = medico;
        this.paciente = paciente;
        this.dataConsulta = dataConsulta;
        this.horario = horario;
        this.status = StatusConsulta.AGENDADA;
    }

    public void realizar(Diagnostico diagnostico, String receita, BigDecimal valorPago) {
        this.diagnostico = diagnostico;
        this.receita = receita;
        this.valorPago = valorPago;
        this.status = StatusConsulta.REALIZADA;
    }

    public void cancelar() {
        this.status = StatusConsulta.CANCELADA;
    }

    public boolean estaAgendada() {
        return status == StatusConsulta.AGENDADA;
    }

    public boolean foiRealizada() {
        return status == StatusConsulta.REALIZADA;
    }

    public boolean foiCancelada() {
        return status == StatusConsulta.CANCELADA;
    }

    public enum StatusConsulta {
        AGENDADA, REALIZADA, CANCELADA
    }
}
