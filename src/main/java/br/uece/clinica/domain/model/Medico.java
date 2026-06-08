package br.uece.clinica.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "medico")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo", discriminatorType = DiscriminatorType.STRING, length = 30)
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "consultas")
public abstract class Medico extends BaseEntity {

    private static final BigDecimal VALOR_PADRAO_CONSULTA = new BigDecimal("100.00");

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false, length = 20)
    private String crm;

    @Column(unique = true, length = 14)
    private String cpf;

    @Column(name = "senha_hash")
    private String senhaHash;

    @Column(nullable = false)
    private String especialidade;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "medico_planos_saude", joinColumns = @JoinColumn(name = "medico_id"))
    @Column(name = "plano")
    private List<String> planosAtendidos = new ArrayList<>();

    @Column(name = "valor_consulta_particular", precision = 10, scale = 2)
    private BigDecimal valorConsultaParticular;

    private String telefone;

    @Email
    private String email;

    @OneToMany(mappedBy = "medico", fetch = FetchType.LAZY)
    private List<Consulta> consultas = new ArrayList<>();

    protected Medico(String nome, String crm, String especialidade, String telefone,
                     String email, BigDecimal valorConsultaParticular) {
        this.nome = nome;
        this.crm = crm;
        this.especialidade = especialidade;
        this.telefone = telefone;
        this.email = email;
        this.valorConsultaParticular = valorConsultaParticular;
    }

    public boolean aceitaPlano(String plano) {
        return planosAtendidos.contains(plano);
    }

    public void adicionarPlano(String plano) {
        if (!planosAtendidos.contains(plano)) {
            planosAtendidos.add(plano);
        }
    }

    public void removerPlano(String plano) {
        planosAtendidos.remove(plano);
    }


    /**
     * Regra de cobrança do sistema:
     * - SUS: consulta gratuita;
     * - qualquer plano privado: consulta paga;
     * - sem plano: consulta paga.
     *
     * A lista de planos atendidos continua existindo para pesquisa/filtro de médicos,
     * mas não zera automaticamente o valor da consulta.
     */
    public BigDecimal getValorConsulta(boolean possuiPlano, String nomePlano) {
        if (ehPlanoSus(nomePlano)) {
            return BigDecimal.ZERO;
        }
        return getValorConsultaParticularEfetivo();
    }

    public BigDecimal getValorConsultaParticularEfetivo() {
        if (valorConsultaParticular == null || valorConsultaParticular.compareTo(BigDecimal.ZERO) <= 0) {
            return VALOR_PADRAO_CONSULTA;
        }
        return valorConsultaParticular;
    }

    private boolean ehPlanoSus(String nomePlano) {
        if (nomePlano == null) {
            return false;
        }
        String normalizado = nomePlano.trim().toLowerCase();
        return "sus".equals(normalizado)
                || "sistema unico de saude".equals(normalizado)
                || "sistema unico de saúde".equals(normalizado)
                || "sistema único de saúde".equals(normalizado);
    }

    public void adicionarPlanosAtendidos(String... planos) {
        if (planos == null) {
            return;
        }
        for (String plano : planos) {
            adicionarPlano(plano);
        }
    }

    public double getMediaAvaliacoes() {
        return consultas.stream()
                .filter(c -> c.getAvaliacao() != null && c.getAvaliacao().getEstrelas() != null)
                .mapToInt(c -> c.getAvaliacao().getEstrelas())
                .average()
                .orElse(0.0);
    }

    public List<Avaliacao> getAvaliacoes() {
        return consultas.stream()
                .map(Consulta::getAvaliacao)
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    public int getMaxPacientesPorDia() {
        return 3;
    }

    public abstract String getTipoEspecialidade();
}
