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

    private String turno;

    private Integer anosExperiencia;

    private boolean ativo;
}