package br.uece.clinica.ui;

/**
 * Representa a tela conceitual de login exigida pela disciplina.
 * A interface real do projeto está em src/main/resources/static.
 */
public class LoginView {

    public String titulo() {
        return "Login de médicos e pacientes";
    }

    public String descricao() {
        return "Tela responsável pela identificação do usuário antes de acessar operações restritas.";
    }
}
