package br.uece.clinica.application.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnfermeiroResponse {

    private UUID id;

    private String nome;

    private String coren;

    private String cpf;

    private String telefone;

    private String email;

    private String especialidade;

    private String turno;

    private Integer anosExperiencia;

    private Integer totalTriagensRealizadas;

    private boolean ativo;
}