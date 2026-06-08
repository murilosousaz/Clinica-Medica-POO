package br.uece.clinica.domain.valueobject;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanoSaude {
    private String nome;
    private String numeroCarnetizacao;
    private boolean ativo;

    public static PlanoSaude naoPossui() {
        return new PlanoSaude("Não tenho", null, false);
    }

    public boolean temCobertura() {
        return ativo && nome != null && !ehSemPlano();
    }

    /**
     * Regra de cobrança definida para o trabalho:
     * somente o SUS torna a consulta gratuita.
     * Planos privados e pacientes sem plano devem gerar cobrança.
     */
    public boolean ehSus() {
        String normalizado = normalizar(nome);
        return "sus".equals(normalizado)
                || "sistema unico de saude".equals(normalizado)
                || "sistema unico de saúde".equals(normalizado)
                || "sistema único de saúde".equals(normalizado);
    }

    public boolean ehSemPlano() {
        String normalizado = normalizar(nome);
        return normalizado.isBlank()
                || "nao tenho".equals(normalizado)
                || "não tenho".equals(normalizado)
                || "sem plano".equals(normalizado);
    }

    public boolean deveGerarCobranca() {
        return !ehSus();
    }

    private String normalizar(String valor) {
        if (valor == null) {
            return "";
        }
        return valor.trim().toLowerCase();
    }
}
