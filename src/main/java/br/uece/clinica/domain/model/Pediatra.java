package br.uece.clinica.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("PEDIATRA")
@Getter
@Setter
@NoArgsConstructor
public class Pediatra extends Medico {


    public Pediatra(String nome, String crm) {
        this(nome, crm, null, null, BigDecimal.ZERO);
    }

    public Pediatra(String nome, String crm, String telefone, String email) {
        this(nome, crm, telefone, email, BigDecimal.ZERO);
    }

    @Column(name = "idade_maxima_paciente")
    private Integer idadeMaximaPaciente = 18;

    public Pediatra(String nome, String crm, String telefone, String email,
                    BigDecimal valorConsultaParticular) {
        super(nome, crm, "Pediatria", telefone, email, valorConsultaParticular);
    }

    @Override
    public String getTipoEspecialidade() {
        return "Pediatria";
    }

    public boolean podeAtender(Paciente paciente) {
        return paciente.getIdade() != null && paciente.getIdade() <= idadeMaximaPaciente;
    }
}
