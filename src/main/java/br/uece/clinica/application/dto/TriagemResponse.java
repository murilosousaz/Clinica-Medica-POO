package br.uece.clinica.application.dto;

import br.uece.clinica.domain.valueobject.PrioridadeSUS;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TriagemResponse {

    private UUID id;

    private UUID pacienteId;

    private String pacienteNome;

    private UUID enfermeiroId;

    private String enfermeiroNome;

    private PrioridadeSUS prioridade;

    private String queixaPrincipal;

    private LocalDateTime dataHora;

    private Double temperatura;

    private Integer frequenciaCardiaca;

    private Integer frequenciaRespiratoria;

    private Double peso;

    private Double altura;

    private String pressaoArterial;
}