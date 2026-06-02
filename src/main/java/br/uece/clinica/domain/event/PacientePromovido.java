package br.uece.clinica.domain.event;

import br.uece.clinica.domain.model.Paciente;
import br.uece.clinica.domain.valueobject.PlanoSaude;
import org.springframework.context.ApplicationEvent;

public class PacientePromovido extends ApplicationEvent {

    private final Paciente paciente;
    private final PlanoSaude novoPlano;

    public PacientePromovido(Object source, Paciente paciente, PlanoSaude novoPlano) {
        super(source);
        this.paciente = paciente;
        this.novoPlano = novoPlano;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public PlanoSaude getNovoPlano() {
        return novoPlano;
    }
}
