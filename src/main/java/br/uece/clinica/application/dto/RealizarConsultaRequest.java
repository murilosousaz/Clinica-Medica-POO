package br.uece.clinica.application.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RealizarConsultaRequest {

    @NotBlank
    private String sintomas;

    @NotBlank
    private String diagnostico;

    private String tratamentoSugerido;

    private String medicamentos;

    @Builder.Default
    private List<String> examesSolicitados = new ArrayList<>();

    private String observacoesGerais;
}
