package br.uece.clinica.domain.exception;

public class EnfermeiroNaoEmTurnoException extends RuntimeException {

    public EnfermeiroNaoEmTurnoException(String enfermeiroNome) {
        super("Enfermeiro " + enfermeiroNome + " não está em turno ativo");
    }

    public EnfermeiroNaoEmTurnoException(String message, boolean raw) {
        super(message);
    }
}
