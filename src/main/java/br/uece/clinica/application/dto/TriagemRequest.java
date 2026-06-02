package br.uece.clinica.application.dto;

import br.uece.clinica.domain.valueobject.PrioridadeSUS;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TriagemRequest {

    @NotNull
    private UUID pacienteId;

    @NotNull
    private UUID enfermeiroId;

    @NotNull
    private PrioridadeSUS prioridade;

    private String queixaPrincipal;

    private Double temperatura;

    private Integer frequenciaCardiaca;

    private Integer frequenciaRespiratoria;

    private Double peso;

    private Double altura;

    private String pressaoArterial;
}