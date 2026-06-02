package br.uece.clinica.application.dto;

import br.uece.clinica.domain.valueobject.PlanoSaude;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PacienteResponse {

    private UUID id;

    private String nome;

    private Integer idade;

    private String cpf;

    private String telefone;

    private String email;

    private PlanoSaude planoSaude;

    private boolean ativo;
}