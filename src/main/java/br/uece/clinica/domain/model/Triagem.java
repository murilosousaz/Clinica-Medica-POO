package br.uece.clinica.domain.model;

import br.uece.clinica.domain.valueobject.PrioridadeSUS;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "triagem")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"paciente", "enfermeiro"})
public class Triagem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "paciente_id", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enfermeiro_id", nullable = false)
    private Enfermeiro enfermeiro;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadeSUS prioridade;

    @Column(name = "queixa_principal", nullable = false, length = 1000)
    private String queijaPrincipal;

    @Column(name = "pressao_arterial", length = 20)
    private String pressaoArterial;

    @Column(name = "frequencia_cardiaca")
    private Integer frequenciaCardiaca;

    private Double temperatura;

    @Column(name = "frequencia_respiratoria")
    private Integer frequenciaRespiratoria;

    private Double peso;

    private Double altura;

    @Column(length = 2000)
    private String observacoes;

    public Triagem(Paciente paciente, Enfermeiro enfermeiro, PrioridadeSUS prioridade,
                   String queijaPrincipal) {
        this.paciente = paciente;
        this.enfermeiro = enfermeiro;
        this.prioridade = prioridade;
        this.queijaPrincipal = queijaPrincipal;
    }

    public double calcularIMC() {
        if (peso != null && altura != null && altura > 0) {
            return peso / (altura * altura);
        }
        return 0.0;
    }

    public boolean sinaisVitaisCompletos() {
        return pressaoArterial != null && frequenciaCardiaca != null
                && temperatura != null && frequenciaRespiratoria != null;
    }
}
