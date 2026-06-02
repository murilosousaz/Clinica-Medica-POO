package br.uece.clinica.domain.event;

import br.uece.clinica.domain.model.Consulta;
import org.springframework.context.ApplicationEvent;

public class ConsultaRealizada extends ApplicationEvent {

    private final Consulta consulta;

    public ConsultaRealizada(Object source, Consulta consulta) {
        super(source);
        this.consulta = consulta;
    }

    public Consulta getConsulta() {
        return consulta;
    }
}
