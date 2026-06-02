package br.uece.clinica.domain.exception;

public class AgendaLotadaException extends RuntimeException {

    public AgendaLotadaException(String medicoNome, String data) {
        super("Agenda do médico " + medicoNome + " está lotada para a data " + data);
    }

    public AgendaLotadaException(String message) {
        super(message);
    }
}
