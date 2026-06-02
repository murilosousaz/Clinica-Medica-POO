package br.uece.clinica.api.controller;

import br.uece.clinica.application.dto.CreateMedicoRequest;
import br.uece.clinica.application.dto.MedicoResponse;
import br.uece.clinica.application.service.MedicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/medicos")
@RequiredArgsConstructor
public class MedicoController {

    private final MedicoService medicoService;

    @GetMapping
    public List<MedicoResponse> listarTodos() {
        return medicoService.listarTodos();
    }

    @GetMapping("/{id}")
    public MedicoResponse buscarPorId(@PathVariable UUID id) {
        return medicoService.buscarPorId(id);
    }

    @GetMapping("/especialidade/{especialidade}")
    public List<MedicoResponse> buscarEspecialidade(
            @PathVariable String especialidade) {

        return medicoService.buscarPorEspecialidade(especialidade);
    }

    @GetMapping("/ranking")
    public List<MedicoResponse> ranking() {
        return medicoService.rankingAvaliacoes();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MedicoResponse cadastrar(
            @Valid @RequestBody CreateMedicoRequest request) {

        return medicoService.salvar(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desativar(@PathVariable UUID id) {
        medicoService.desativar(id);
    }
}