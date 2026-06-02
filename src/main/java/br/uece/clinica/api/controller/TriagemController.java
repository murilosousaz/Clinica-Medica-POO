package br.uece.clinica.api.controller;

import br.uece.clinica.application.dto.TriagemRequest;
import br.uece.clinica.application.dto.TriagemResponse;
import br.uece.clinica.application.service.TriagemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/triagens")
@RequiredArgsConstructor
public class TriagemController {

    private final TriagemService triagemService;

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