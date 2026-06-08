package br.uece.clinica.domain.model;

import br.uece.clinica.domain.valueobject.PlanoSaude;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "paciente")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = {"consultas", "triagens", "contas"})
public class Paciente extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String nome;

    private Integer idade;

    @Column(unique = true, nullable = false, length = 14)
    private String cpf;

    private String telefone;

    @Email
    private String email;

    @Column(name = "senha_hash")
    private String senhaHash;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "nome", column = @Column(name = "plano_saude_nome")),
        @AttributeOverride(name = "numeroCarnetizacao", column = @Column(name = "plano_saude_numero")),
        @AttributeOverride(name = "ativo", column = @Column(name = "plano_saude_ativo"))
    })
    private PlanoSaude planoSaude;

    @OneToMany(mappedBy = "paciente", fetch = FetchType.LAZY)
    private List<Consulta> consultas = new ArrayList<>();

    @OneToMany(mappedBy = "paciente", fetch = FetchType.LAZY)
    private List<Triagem> triagens = new ArrayList<>();

    @OneToMany(mappedBy = "paciente", fetch = FetchType.LAZY)
    private List<Conta> contas = new ArrayList<>();

    public Paciente(String nome, Integer idade, String cpf, String telefone, String email) {
        this(nome, idade, cpf, telefone, email, PlanoSaude.naoPossui());
    }

    public Paciente(String nome, Integer idade, String cpf, String telefone,
                    String email, PlanoSaude planoSaude) {
        this.nome = nome;
        this.idade = idade;
        this.cpf = cpf;
        this.telefone = telefone;
        this.email = email;
        this.planoSaude = planoSaude != null ? planoSaude : PlanoSaude.naoPossui();
    }

    public boolean temPlanoSaude() {
        return planoSaude != null && planoSaude.temCobertura();
    }

    public boolean usaSus() {
        return planoSaude != null && planoSaude.ehSus();
    }

    public boolean devePagarConsulta() {
        return planoSaude == null || planoSaude.deveGerarCobranca();
    }

    public void atualizarPlanoSaude(PlanoSaude planoSaude) {
        this.planoSaude = planoSaude != null ? planoSaude : PlanoSaude.naoPossui();
    }

    public List<Consulta> getHistoricoConsultas() {
        return consultas;
    }

    public int contarConsultasRealizadas() {
        return (int) consultas.stream()
                .filter(c -> c.getStatus() == Consulta.StatusConsulta.REALIZADA)
                .count();
    }
}
