package br.uece.clinica.application.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContaResponse {
    private UUID id;
    private UUID pacienteId;
    private String pacienteNome;
    private UUID consultaId;
    private BigDecimal valor;
    private String descricao;
    private LocalDate dataVencimento;
    private LocalDate dataPagamento;
    private String situacao;
}
