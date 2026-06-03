package br.uece.clinica.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AvaliacaoRequest {

    @NotNull
    private UUID consultaId;

    private String texto;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer estrelas;
}
