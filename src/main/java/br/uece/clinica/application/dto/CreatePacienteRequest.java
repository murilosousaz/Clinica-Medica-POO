package br.uece.clinica.application.dto;

import br.uece.clinica.domain.valueobject.PlanoSaude;
import jakarta.validation.constraints.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePacienteRequest {

    @NotBlank
    private String nome;

    @Min(0)
    private Integer idade;

    @NotBlank
    private String cpf;

    @NotBlank
    private String telefone;

    @Email
    private String email;

    private PlanoSaude planoSaude;
}