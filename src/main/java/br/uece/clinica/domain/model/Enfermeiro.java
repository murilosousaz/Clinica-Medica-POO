package br.uece.clinica.domain.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "enfermeiro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Enfermeiro extends BaseEntity {
    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "coren", nullable = false, unique = true, length = 30)
    private String coren;

    @Column(name = "cpf", unique = true, length = 14)
    private String cpf;

    @Column(name = "senha_hash", length = 100)
    private String senhaHash;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "especialidade", length = 100)
    private String especialidade;

    @Column(name = "turno", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Turno turno;

    @Column(name = "anos_experiencia")
    private Integer anosExperiencia = 0;

    @Column(name = "total_triagens_realizadas")
    private Integer totalTriagensRealizadas = 0;

    @Column(name = "ultimo_acesso")
    private LocalDateTime ultimoAcesso;

    @OneToMany(mappedBy = "enfermeiro", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Triagem> triagens = new ArrayList<>();

    public enum Turno {
        MATUTINO("Matutino - 06:00 às 12:00"),
        VESPERTINO("Vespertino - 12:00 às 18:00"),
        NOTURNO("Noturno - 18:00 às 06:00");

        private final String descricao;

        Turno(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public Enfermeiro(String nome, String coren, String telefone, String email, String especialidade, Turno turno) {
        this.nome = nome;
        this.coren = coren;
        this.telefone = telefone;
        this.email = email;
        this.especialidade = especialidade;
        this.turno = turno;
        this.totalTriagensRealizadas = 0;
    }

    public void registrarTriagem(Triagem triagem) {
        this.triagens.add(triagem);
        this.totalTriagensRealizadas++;
        this.ultimoAcesso = LocalDateTime.now();
    }

    public void atualizarUltimoAcesso() {
        this.ultimoAcesso = LocalDateTime.now();
    }

    public boolean estaEmTurno(LocalDateTime horario) {
        int hora = horario.getHour();
        return switch (turno) {
            case MATUTINO -> hora >= 6 && hora < 12;
            case VESPERTINO -> hora >= 12 && hora < 18;
            case NOTURNO -> hora >= 18 || hora < 6;
        };
    }

    public boolean podeRealizarTriagem() {
        return this.isAtivo() && this.estaEmTurno(LocalDateTime.now());
    }
}