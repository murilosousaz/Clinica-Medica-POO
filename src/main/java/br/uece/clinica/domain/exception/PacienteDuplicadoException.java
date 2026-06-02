package br.uece.clinica.domain.exception;

public class PacienteDuplicadoException extends RuntimeException {

    public PacienteDuplicadoException(String cpf) {
        super("Paciente com CPF " + cpf + " já está cadastrado");
    }

    public PacienteDuplicadoException(String message, boolean raw) {
        super(message);
    }
}
