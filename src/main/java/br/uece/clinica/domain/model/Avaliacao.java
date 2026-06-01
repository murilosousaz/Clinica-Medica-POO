package br.uece.clinica.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "avaliacao")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "consulta")
public class Avaliacao extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "consulta_id", nullable = false, unique = true)
    private Consulta consulta;

    @Column(length = 2000)
    private String texto;

    @Min(1)
    @Max(5)
    @Column(nullable = false)
    private Integer estrelas;

    public Avaliacao(Consulta consulta, String texto, Integer estrelas) {
        this.consulta = consulta;
        this.texto = texto;
        this.estrelas = estrelas;
    }

    public boolean isPositiva() {
        return estrelas != null && estrelas >= 4;
    }

    public boolean isNegativa() {
        return estrelas != null && estrelas < 3;
    }

    public boolean isNeutra() {
        return estrelas != null && estrelas == 3;
    }
}
