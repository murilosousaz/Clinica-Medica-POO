package br.uece.clinica.application.dto;

import br.uece.clinica.domain.model.Enfermeiro;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnfermeiroResponse {
    private UUID id;
    private String nome;
    private String coren;
    private String telefone;
    private String email;
    private String especialidade;
    private String turno;
    private Integer totalTriagensRealizadas;
    private LocalDateTime ultimoAcesso;
    private boolean ativo;
    private LocalDateTime dataCriacao;

    public static EnfermeiroResponse fromEntity(Enfermeiro enfermeiro) {
        return EnfermeiroResponse.builder()
                .id(enfermeiro.getId())
                .nome(enfermeiro.getNome())
                .coren(enfermeiro.getCoren())
                .telefone(enfermeiro.getTelefone())
                .email(enfermeiro.getEmail())
                .especialidade(enfermeiro.getEspecialidade())
                .turno(enfermeiro.getTurno().getDescricao())
                .totalTriagensRealizadas(enfermeiro.getTotalTriagensRealizadas())
                .ultimoAcesso(enfermeiro.getUltimoAcesso())
                .ativo(enfermeiro.isAtivo())
                .dataCriacao(enfermeiro.getDataCriacao())
                .build();
    }
}