package br.uece.clinica.domain.event;

import br.uece.clinica.domain.model.Triagem;
import org.springframework.context.ApplicationEvent;

public class TriagemRealizada extends ApplicationEvent {

    private final Triagem triagem;

    public TriagemRealizada(Object source, Triagem triagem) {
        super(source);
        this.triagem = triagem;
    }

    public Triagem getTriagem() {
        return triagem;
    }
}
