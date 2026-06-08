package br.uece.clinica.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMedicoRequest {

    @NotBlank
    private String nome;

    @NotBlank
    private String crm;

    private String cpf;

    private String senha;

    @NotBlank
    private String especialidade;

    @Builder.Default
    private List<String> planosAtendidos = new ArrayList<>();

    private BigDecimal valorConsultaParticular;
}
