package br.uece.clinica.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Table(name = "lista_espera")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"paciente", "medico"})
public class ListaEspera extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "medico_id", nullable = false)
    private Medico medico;

    @Column(name = "data_consulta", nullable = false)
    private LocalDate dataConsulta;

    @Column(length = 10)
    private String horarioDesejado;

    @Column(length = 1000)
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusListaEspera status = StatusListaEspera.ESPERANDO;

    @Column(length = 1000)
    private String notificacao;

    public ListaEspera(Paciente paciente, Medico medico, LocalDate dataConsulta, String horarioDesejado, String observacoes) {
        this.paciente = paciente;
        this.medico = medico;
        this.dataConsulta = dataConsulta;
        this.horarioDesejado = horarioDesejado;
        this.observacoes = observacoes;
        this.status = StatusListaEspera.ESPERANDO;
    }

    public void promover(String horarioPromovido) {
        this.status = StatusListaEspera.PROMOVIDO;
        this.notificacao = "Paciente promovido automaticamente da lista de espera para o horário " + horarioPromovido + ".";
    }

    public void cancelar() {
        this.status = StatusListaEspera.CANCELADO;
    }

    public enum StatusListaEspera {
        ESPERANDO, PROMOVIDO, CANCELADO
    }
}
