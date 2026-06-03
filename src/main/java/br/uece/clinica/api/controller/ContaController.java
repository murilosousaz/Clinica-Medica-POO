package br.uece.clinica.api.controller;

import br.uece.clinica.application.service.ContaService;
import br.uece.clinica.domain.model.Conta;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@Tag(name = "Contas", description = "Endpoints para acompanhamento de contas, débitos e pagamentos.")
@RequestMapping("/api/contas")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;

    @GetMapping("/{id}")
    public Conta buscarPorId(@PathVariable UUID id) {
        return contaService.buscarPorId(id);
    }

    @GetMapping("/pendentes")
    public List<Conta> listarPendentes() {
        return contaService.listarPendentes();
    }

    @GetMapping("/vencidas")
    public List<Conta> listarVencidas() {
        return contaService.listarVencidas();
    }

    @GetMapping("/paciente/{pacienteId}/debito")
    public Map<String, Object> debitoPaciente(@PathVariable UUID pacienteId) {
        BigDecimal debito = contaService.calcularDebito(pacienteId);
        return Map.of("pacienteId", pacienteId, "debito", debito == null ? BigDecimal.ZERO : debito);
    }

    @PutMapping("/{id}/pagar")
    public Conta pagar(@PathVariable UUID id) {
        return contaService.pagar(id);
    }
}
