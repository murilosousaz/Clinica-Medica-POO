package br.uece.clinica.domain.event;

import br.uece.clinica.domain.model.Consulta;
import org.springframework.context.ApplicationEvent;

public class ConsultaCancelada extends ApplicationEvent {

    private final Consulta consulta;
    private final String motivo;

    public ConsultaCancelada(Object source, Consulta consulta, String motivo) {
        super(source);
        this.consulta = consulta;
        this.motivo = motivo;
    }

    public Consulta getConsulta() {
        return consulta;
    }

    public String getMotivo() {
        return motivo;
    }
}
