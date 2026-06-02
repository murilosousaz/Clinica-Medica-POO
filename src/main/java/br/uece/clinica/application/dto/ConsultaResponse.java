package br.uece.clinica.application.dto;

import lombok.*;

import java.time.LocalDateTime;
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

    private LocalDateTime dataHora;

    private String status;

    private String observacoes;
}