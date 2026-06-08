package br.uece.clinica.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEnfermeiroRequest {

    @NotBlank
    private String nome;

    @NotBlank
    private String coren;

    @NotBlank
    private String cpf;

    @NotBlank
    private String senha;

    private String telefone;

    private String email;

    private String especialidade;

    @NotBlank
    private String turno;

    private Integer anosExperiencia;
}