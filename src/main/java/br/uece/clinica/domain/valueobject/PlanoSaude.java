package br.uece.clinica.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Embeddable
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class PlanoSaude {
    private String nome;
    private String numeroCarnetizacao;
    private boolean ativo;

    public static PlanoSaude naoPossui() {
        return new PlanoSaude("Não tenho", null, false);
    }

    public boolean temCobertura() {
        return ativo && nome != null && !nome.equals("Não tenho");
    }
}