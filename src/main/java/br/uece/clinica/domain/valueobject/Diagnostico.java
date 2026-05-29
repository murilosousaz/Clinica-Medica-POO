package br.uece.clinica.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Embeddable
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Diagnostico {
    private String sintomas;
    private String diagnosticoTexto;
    private String tratamentoSugerido;
    private String observacoes;

    public boolean isValido() {
        return sintomas != null && !sintomas.isBlank() &&
                diagnosticoTexto != null && !diagnosticoTexto.isBlank();
    }
}