package br.uece.clinica.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("CLINICO_GERAL")
@Getter
@Setter
@NoArgsConstructor
public class ClinicoGeral extends Medico {

    public ClinicoGeral(String nome, String crm) {
        this(nome, crm, null, null, BigDecimal.ZERO);
    }

    public ClinicoGeral(String nome, String crm, String telefone, String email) {
        this(nome, crm, telefone, email, BigDecimal.ZERO);
    }

    public ClinicoGeral(String nome, String crm, String telefone, String email,
                        BigDecimal valorConsultaParticular) {
        super(nome, crm, "Clínico Geral", telefone, email, valorConsultaParticular);
    }

    @Override
    public String getTipoEspecialidade() {
        return "Clínico Geral";
    }
}
