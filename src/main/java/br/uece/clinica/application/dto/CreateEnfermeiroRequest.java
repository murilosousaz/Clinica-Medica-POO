package br.uece.clinica.application.dto;

import br.uece.clinica.domain.model.Enfermeiro;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEnfermeiroRequest {
    @NotBlank(message = "Nome é obrigatório")
    private String nome;

    @NotBlank(message = "COREN é obrigatório")
    private String coren;

    @NotBlank(message = "Telefone é obrigatório")
    private String telefone;

    @Email(message = "Email deve ser válido")
    private String email;

    @NotBlank(message = "Especialidade é obrigatória")
    private String especialidade;

    @NotNull(message = "Turno é obrigatório")
    private Enfermeiro.Turno turno;
}