package br.uece.clinica.application.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultaRequest {

    @NotNull
    private UUID pacienteId;

    @NotNull
    private UUID medicoId;

    @NotNull
    private LocalDateTime dataHora;

    private String observacoes;
}