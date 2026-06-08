package br.uece.clinica.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    /**
     * CPF do paciente ou do médico.
     */
    @NotBlank
    private String cpf;

    /**
     * Senha cadastrada para o usuário.
     */
    @NotBlank
    private String senha;

    /**
     * Opcional: PACIENTE ou MEDICO. Quando vazio, o sistema tenta localizar primeiro paciente e depois médico.
     */
    private String perfil;
}
