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
    private String turno;

    private Integer anosExperiencia;
}