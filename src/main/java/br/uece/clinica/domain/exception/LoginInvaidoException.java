package br.uece.clinica.domain.exception;

public class LoginInvaidoException extends RuntimeException {

    public LoginInvaidoException() {
        super("Credenciais de login inválidas");
    }

    public LoginInvaidoException(String message) {
        super(message);
    }
}
