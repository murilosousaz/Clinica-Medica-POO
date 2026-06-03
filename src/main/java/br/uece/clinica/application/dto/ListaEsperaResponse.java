package br.uece.clinica.application.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListaEsperaResponse {
    private UUID id;
    private UUID pacienteId;
    private String pacienteNome;
    private UUID medicoId;
    private String medicoNome;
    private LocalDate dataConsulta;
    private String horarioDesejado;
    private String status;
    private String notificacao;
}
