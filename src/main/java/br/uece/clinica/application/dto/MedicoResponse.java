package br.uece.clinica.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicoResponse {

    private UUID id;

    private String nome;

    private String crm;

    private String cpf;

    private String especialidade;

    @Builder.Default
    private List<String> planosAtendidos = new ArrayList<>();

    private BigDecimal valorConsultaParticular;

    private Double mediaAvaliacoes;

    private Integer totalAvaliacoes;

    private Integer maxPacientesPorDia;

    private boolean ativo;
}
