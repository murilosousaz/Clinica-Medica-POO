package br.uece.clinica.application.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvaliacaoResponse {
    private UUID id;
    private UUID consultaId;
    private UUID medicoId;
    private String medicoNome;
    private UUID pacienteId;
    private String pacienteNome;
    private String texto;
    private Integer estrelas;
}
