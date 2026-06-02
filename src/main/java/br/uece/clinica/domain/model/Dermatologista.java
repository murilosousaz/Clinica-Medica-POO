package br.uece.clinica.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("DERMATOLOGISTA")
@Getter
@Setter
@NoArgsConstructor
public class Dermatologista extends Medico {

    @Column(name = "realiza_procedimentos_esteticos")
    private Boolean realizaProcedimentosEsteticos = false;

    public Dermatologista(String nome, String crm, String telefone, String email,
                          BigDecimal valorConsultaParticular) {
        super(nome, crm, "Dermatologia", telefone, email, valorConsultaParticular);
    }

    @Override
    public String getTipoEspecialidade() {
        return "Dermatologia";
    }
}
