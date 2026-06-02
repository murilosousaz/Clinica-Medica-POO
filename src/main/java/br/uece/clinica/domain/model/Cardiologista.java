package br.uece.clinica.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("CARDIOLOGISTA")
@Getter
@Setter
@NoArgsConstructor
public class Cardiologista extends Medico {

    @Column(name = "realiza_ecocardiograma")
    private Boolean realizaEcocardiograma = false;

    public Cardiologista(String nome, String crm, String telefone, String email,
                         BigDecimal valorConsultaParticular) {
        super(nome, crm, "Cardiologia", telefone, email, valorConsultaParticular);
    }

    @Override
    public String getTipoEspecialidade() {
        return "Cardiologia";
    }
}
