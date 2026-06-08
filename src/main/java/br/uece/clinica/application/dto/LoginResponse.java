package br.uece.clinica.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class LoginResponse {
    private String token;
    private String perfil;
    private UUID usuarioId;
    private String nome;
    private String cpf;
    private String mensagem;
}
