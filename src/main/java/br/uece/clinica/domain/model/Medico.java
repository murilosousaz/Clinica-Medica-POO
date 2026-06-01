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

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false, length = 20)
    private String crm;

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

    public abstract String getTipoEspecialidade();
}
