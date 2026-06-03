package br.uece.clinica.ui;

/**
 * Representa a tela de triagem. A versão navegável está no frontend web.
 */
public class TriagemView {

    public String titulo() {
        return "Registro de triagem";
    }

    public String camposPrincipais() {
        return "Paciente, enfermeiro, prioridade, queixa principal e sinais vitais.";
    }
}
