package br.uece.clinica.api.controller;

import br.uece.clinica.application.dto.TriagemRequest;
import br.uece.clinica.application.dto.TriagemResponse;
import br.uece.clinica.application.service.TriagemService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Triagens", description = "Endpoints para registro, fila e consulta de triagens.")
@RequestMapping("/api/triagens")
@RequiredArgsConstructor
public class TriagemController {

    private final TriagemService triagemService;

    @GetMapping
    public List<TriagemResponse> listarTodas() {
        return triagemService.listarTodas();
    }

    @GetMapping("/{id}")
    public TriagemResponse buscarPorId(@PathVariable UUID id) {
        return triagemService.buscarPorId(id);
    }

    @GetMapping("/hoje")
    public List<TriagemResponse> listarTriagensDoDia() {
        return triagemService.listarTriagensDoDia();
    }

    @GetMapping("/emergencias/hoje")
    public List<TriagemResponse> listarEmergenciasHoje() {
        return triagemService.listarTriagensEmergenciaHoje();
    }

    @GetMapping("/fila/tamanho")
    public int tamanhoFila() {
        return triagemService.tamanhoFila();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TriagemResponse registrar(
            @Valid @RequestBody TriagemRequest request) {

        return triagemService.registrar(request);
    }

    @GetMapping("/fila/proximo")
    public TriagemResponse proximoDaFila() {
        return triagemService.obterProximaDaFila();
    }

    @PostMapping("/fila/chamar")
    public TriagemResponse chamarPaciente() {
        return triagemService.chamarProximoPaciente();
    }
}
