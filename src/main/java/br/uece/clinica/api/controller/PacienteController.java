package br.uece.clinica.api.controller;

import br.uece.clinica.application.dto.CreatePacienteRequest;
import br.uece.clinica.application.dto.PacienteResponse;
import br.uece.clinica.application.service.PacienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/pacientes")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;

    @GetMapping
    public List<PacienteResponse> listarTodos() {
        return pacienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public PacienteResponse buscarPorId(@PathVariable UUID id) {
        return pacienteService.buscarPorId(id);
    }

    @GetMapping("/buscar")
    public List<PacienteResponse> buscarPorNome(
            @RequestParam String nome) {

        return pacienteService.buscarPorNome(nome);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PacienteResponse cadastrar(
            @Valid @RequestBody CreatePacienteRequest request) {

        return pacienteService.cadastrar(request);
    }

    @PutMapping("/{id}")
    public PacienteResponse atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody CreatePacienteRequest request) {

        return pacienteService.atualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable UUID id) {
        pacienteService.excluir(id);
    }
}