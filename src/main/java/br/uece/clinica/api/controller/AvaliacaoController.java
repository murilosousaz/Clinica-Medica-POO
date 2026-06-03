package br.uece.clinica.api.controller;

import br.uece.clinica.application.dto.AvaliacaoRequest;
import br.uece.clinica.application.dto.AvaliacaoResponse;
import br.uece.clinica.application.service.AvaliacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    @GetMapping
    public List<AvaliacaoResponse> listarTodas() {
        return avaliacaoService.listarTodas();
    }

    @GetMapping("/{id}")
    public AvaliacaoResponse buscarPorId(@PathVariable UUID id) {
        return avaliacaoService.buscarPorId(id);
    }

    @GetMapping("/medico/{medicoId}")
    public List<AvaliacaoResponse> listarPorMedico(@PathVariable UUID medicoId) {
        return avaliacaoService.listarMedico(medicoId);
    }

    @GetMapping("/medico/{medicoId}/media")
    public Map<String, Object> mediaMedico(@PathVariable UUID medicoId) {
        return Map.of("medicoId", medicoId, "media", avaliacaoService.mediaMedico(medicoId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AvaliacaoResponse avaliar(@Valid @RequestBody AvaliacaoRequest request) {
        return avaliacaoService.avaliar(request);
    }
}
