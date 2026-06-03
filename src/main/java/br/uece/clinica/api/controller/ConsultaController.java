package br.uece.clinica.api.controller;

import br.uece.clinica.application.dto.ConsultaRequest;
import br.uece.clinica.application.dto.ConsultaResponse;
import br.uece.clinica.application.dto.ListaEsperaResponse;
import br.uece.clinica.application.dto.RealizarConsultaRequest;
import br.uece.clinica.application.service.ConsultaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@Tag(name = "Consultas", description = "Endpoints de agendamento, cancelamento, realização, prontuário e lista de espera.")
@RequestMapping("/api/consultas")
@RequiredArgsConstructor
public class ConsultaController {

    private final ConsultaService consultaService;

    @GetMapping
    public List<ConsultaResponse> listarTodas() {
        return consultaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ConsultaResponse buscarPorId(@PathVariable UUID id) {
        return consultaService.buscarPorId(id);
    }

    @GetMapping("/paciente/{pacienteId}")
    public List<ConsultaResponse> listarPaciente(
            @PathVariable UUID pacienteId) {

        return consultaService.listarPorPaciente(pacienteId);
    }

    @GetMapping("/paciente/{pacienteId}/prontuario")
    public List<ConsultaResponse> prontuarioPaciente(@PathVariable UUID pacienteId) {
        return consultaService.prontuarioPaciente(pacienteId);
    }

    @GetMapping("/medico/{medicoId}")
    public List<ConsultaResponse> listarMedico(
            @PathVariable UUID medicoId) {

        return consultaService.listarPorMedico(medicoId);
    }

    @GetMapping("/hoje")
    public List<ConsultaResponse> consultasHoje() {
        return consultaService.consultasHoje();
    }

    @GetMapping("/periodo")
    public List<ConsultaResponse> periodo(
            @RequestParam LocalDate inicio,
            @RequestParam LocalDate fim) {

        return consultaService.consultasPeriodo(inicio, fim);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ConsultaResponse agendar(
            @Valid @RequestBody ConsultaRequest request) {

        return consultaService.agendar(request);
    }

    @PutMapping("/{id}/cancelar")
    public ConsultaResponse cancelar(@PathVariable UUID id) {
        return consultaService.cancelar(id);
    }

    @PutMapping("/{id}/realizar")
    public ConsultaResponse realizar(
            @PathVariable UUID id,
            @Valid @RequestBody RealizarConsultaRequest request) {
        return consultaService.realizar(id, request);
    }

    @GetMapping("/lista-espera")
    public List<ListaEsperaResponse> listarFilaEspera() {
        return consultaService.listarFilaEspera();
    }
}