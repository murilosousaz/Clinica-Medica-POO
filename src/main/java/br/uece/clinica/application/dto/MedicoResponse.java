package br.uece.clinica.application.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicoResponse {

    private UUID id;

    private String nome;

    private String crm;

    private String especialidade;

    private boolean ativo;
}