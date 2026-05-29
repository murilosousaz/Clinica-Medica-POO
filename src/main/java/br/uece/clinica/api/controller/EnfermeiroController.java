package br.uece.clinica.api.controller;

import br.uece.clinica.application.dto.CreateEnfermeiroRequest;
import br.uece.clinica.application.dto.EnfermeiroResponse;
import br.uece.clinica.application.service.EnfermeiroService;
import br.uece.clinica.domain.model.Enfermeiro;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/enfermeiros")
@RequiredArgsConstructor
public class EnfermeiroController {
    private final EnfermeiroService enfermeiroService;

    @PostMapping
    public ResponseEntity<EnfermeiroResponse> criar(@Valid @RequestBody CreateEnfermeiroRequest request) {
        try {
            Enfermeiro enfermeiro = enfermeiroService.criarEnfermeiro(
                    request.getNome(),
                    request.getCoren(),
                    request.getTelefone(),
                    request.getEmail(),
                    request.getEspecialidade(),
                    request.getTurno()
            );
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(EnfermeiroResponse.fromEntity(enfermeiro));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EnfermeiroResponse> obterPorId(@PathVariable UUID id) {
        try {
            Enfermeiro enfermeiro = enfermeiroService.obterPorId(id);
            return ResponseEntity.ok(EnfermeiroResponse.fromEntity(enfermeiro));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/coren/{coren}")
    public ResponseEntity<EnfermeiroResponse> obterPorCoren(@PathVariable String coren) {
        try {
            Enfermeiro enfermeiro = enfermeiroService.obterPorCoren(coren);
            return ResponseEntity.ok(EnfermeiroResponse.fromEntity(enfermeiro));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<EnfermeiroResponse>> listarTodos() {
        List<Enfermeiro> enfermeiros = enfermeiroService.listarTodos();
        List<EnfermeiroResponse> responses = enfermeiros.stream()
                .map(EnfermeiroResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/turno/{turno}")
    public ResponseEntity<List<EnfermeiroResponse>> listarPorTurno(@PathVariable String turno) {
        try {
            Enfermeiro.Turno turnoEnum = Enfermeiro.Turno.valueOf(turno.toUpperCase());
            List<Enfermeiro> enfermeiros = enfermeiroService.listarPorTurno(turnoEnum);
            List<EnfermeiroResponse> responses = enfermeiros.stream()
                    .map(EnfermeiroResponse::fromEntity)
                    .toList();
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/especialidade/{especialidade}")
    public ResponseEntity<List<EnfermeiroResponse>> listarPorEspecialidade(@PathVariable String especialidade) {
        List<Enfermeiro> enfermeiros = enfermeiroService.listarPorEspecialidade(especialidade);
        List<EnfermeiroResponse> responses = enfermeiros.stream()
                .map(EnfermeiroResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/ranking/experiencia")
    public ResponseEntity<List<EnfermeiroResponse>> listarMaisExperientes() {
        List<Enfermeiro> enfermeiros = enfermeiroService.listarMaisExperientes();
        List<EnfermeiroResponse> responses = enfermeiros.stream()
                .map(EnfermeiroResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/turno-ativo")
    public ResponseEntity<List<EnfermeiroResponse>> listarEmTurno() {
        List<Enfermeiro> enfermeiros = enfermeiroService.listarEmTurno();
        List<EnfermeiroResponse> responses = enfermeiros.stream()
                .map(EnfermeiroResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EnfermeiroResponse>> buscarPorNome(@RequestParam String nome) {
        if (nome == null || nome.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        List<Enfermeiro> enfermeiros = enfermeiroService.buscarPorNome(nome);
        List<EnfermeiroResponse> responses = enfermeiros.stream()
                .map(EnfermeiroResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnfermeiroResponse> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody CreateEnfermeiroRequest request
    ) {
        try {
            Enfermeiro enfermeiro = enfermeiroService.atualizarEnfermeiro(
                    id,
                    request.getNome(),
                    request.getTelefone(),
                    request.getEmail(),
                    request.getEspecialidade(),
                    request.getTurno()
            );
            return ResponseEntity.ok(EnfermeiroResponse.fromEntity(enfermeiro));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable UUID id) {
        try {
            enfermeiroService.desativarEnfermeiro(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/ativar")
    public ResponseEntity<Void> ativar(@PathVariable UUID id) {
        try {
            enfermeiroService.ativarEnfermeiro(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/estatisticas")
    public ResponseEntity<Map<String, Object>> obterEstatisticas(@PathVariable UUID id) {
        try {
            Map<String, Object> stats = enfermeiroService.obterEstatisticasEnfermeiro(id);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/estatisticas/geral")
    public ResponseEntity<Map<String, Integer>> obterEstatisticasGerais() {
        Map<String, Integer> stats = enfermeiroService.obterEstatisticasGerais();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/estatisticas/detalhadas")
    public ResponseEntity<List<Map<String, Object>>> listarTodasAsEstatisticas() {
        List<Map<String, Object>> stats = enfermeiroService.listarTodasAsEstatisticas();
        return ResponseEntity.ok(stats);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        try {
            enfermeiroService.desativarEnfermeiro(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}