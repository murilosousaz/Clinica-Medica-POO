package br.uece.clinica.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultaResponse {

    private UUID id;
    private UUID pacienteId;
    private String pacienteNome;
    private UUID medicoId;
    private String medicoNome;
    private String medicoEspecialidade;
    private LocalDateTime dataHora;
    private String status;
    private String observacoes;

    private String sintomas;
    private String diagnostico;
    private String tratamentoSugerido;
    private String medicamentos;

    @Builder.Default
    private List<String> examesSolicitados = new ArrayList<>();

    private BigDecimal valorPago;
    private Integer avaliacaoEstrelas;
    private String avaliacaoTexto;
}
