package br.uece.clinica.domain.exception;

public class LoginInvalidoException extends RuntimeException {

    public LoginInvalidoException() {
        super("Credenciais de login inválidas");
    }

    public LoginInvalidoException(String message) {
        super(message);
    }
}
