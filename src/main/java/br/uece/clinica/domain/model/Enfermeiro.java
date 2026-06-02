package br.uece.clinica.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "enfermeiro")
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "triagens")
public class Enfermeiro extends BaseEntity {

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false, length = 20)
    private String coren;

    private String telefone;

    @Email
    private String email;

    private String especialidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Turno turno;

    @Column(name = "total_triagens_realizadas", nullable = false)
    private int totalTriagensRealizadas = 0;

    @Column(name = "ultimo_acesso")
    private LocalDateTime ultimoAcesso;

    @OneToMany(mappedBy = "enfermeiro", fetch = FetchType.LAZY)
    private List<Triagem> triagens = new ArrayList<>();

    public Enfermeiro(String nome, String coren, String telefone, String email,
                      String especialidade, Turno turno) {
        this.nome = nome;
        this.coren = coren;
        this.telefone = telefone;
        this.email = email;
        this.especialidade = especialidade;
        this.turno = turno;
        this.setAtivo(true);
    }

    public boolean podeRealizarTriagem() {
        return isAtivo() && turno != null;
    }

    public void registrarTriagem(Triagem triagem) {
        triagens.add(triagem);
        this.totalTriagensRealizadas++;
        this.ultimoAcesso = LocalDateTime.now();
    }

    public enum Turno {
        MATUTINO("Matutino"),
        VESPERTINO("Vespertino"),
        NOTURNO("Noturno");

        private final String descricao;

        Turno(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
