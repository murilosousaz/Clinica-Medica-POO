package br.uece.clinica.api.controller;

import br.uece.clinica.application.dto.CreateEnfermeiroRequest;
import br.uece.clinica.application.dto.EnfermeiroResponse;
import br.uece.clinica.application.service.EnfermeiroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/enfermeiros")
@RequiredArgsConstructor
public class EnfermeiroController {

    private final EnfermeiroService enfermeiroService;

    @GetMapping
    public List<EnfermeiroResponse> listarTodos() {
        return enfermeiroService.listarTodos();
    }

    @GetMapping("/{id}")
    public EnfermeiroResponse buscarPorId(@PathVariable UUID id) {
        return enfermeiroService.buscarPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EnfermeiroResponse cadastrar(
            @Valid @RequestBody CreateEnfermeiroRequest request) {

        return enfermeiroService.cadastrar(request);
    }

    @PutMapping("/{id}")
    public EnfermeiroResponse atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody CreateEnfermeiroRequest request) {

        return enfermeiroService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remover(@PathVariable UUID id) {
        enfermeiroService.remover(id);
    }
}