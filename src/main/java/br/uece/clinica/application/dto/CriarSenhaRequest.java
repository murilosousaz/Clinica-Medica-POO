package br.uece.clinica.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CriarSenhaRequest {
    @NotBlank
    private String cpf;

    @NotBlank
    private String senha;

    private String perfil;
}
