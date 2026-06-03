package br.uece.clinica.application.service;

import br.uece.clinica.application.dto.ConsultaRequest;
import br.uece.clinica.application.dto.ConsultaResponse;
import br.uece.clinica.application.mapper.ConsultaMapper;
import br.uece.clinica.domain.exception.AgendaLotadaException;
import br.uece.clinica.domain.model.Consulta;
import br.uece.clinica.domain.model.Medico;
import br.uece.clinica.domain.model.Paciente;
import br.uece.clinica.domain.repository.ConsultaRepository;
import br.uece.clinica.domain.repository.MedicoRepository;
import br.uece.clinica.domain.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    public ConsultaResponse agendar(ConsultaRequest request) {
        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        Medico medico = medicoRepository.findById(request.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        List<Consulta> consultas = consultaRepository.findConsultasDoMedicoNoDia(
                medico,
                request.getDataHora().toLocalDate()
        );

        if (consultas.size() >= 20) {
            throw new AgendaLotadaException("Agenda do médico lotada para este dia");
        }

        Consulta consulta = ConsultaMapper.toEntity(request, paciente, medico);
        Consulta consultaSalva = consultaRepository.save(consulta);
        return toResponse(consultaSalva);
    }

    @Transactional(readOnly = true)
    public ConsultaResponse buscarPorId(UUID id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
        return toResponse(consulta);
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponse> listarTodas() {
        return consultaRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponse> listarPorPaciente(UUID pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        return consultaRepository.findByPaciente(paciente).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponse> listarPorMedico(UUID medicoId) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        return consultaRepository.findByMedico(medico).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ConsultaResponse cancelar(UUID id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        consulta.cancelar();
        Consulta consultaSalva = consultaRepository.save(consulta);
        return toResponse(consultaSalva);
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponse> consultasHoje() {
        return consultaRepository.findConsultasHojeAgendadas().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponse> consultasPeriodo(LocalDate inicio, LocalDate fim) {
        return consultaRepository.findConsultasEmPeriodo(inicio, fim).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private ConsultaResponse toResponse(Consulta consulta) {
        return ConsultaResponse.builder()
                .id(consulta.getId())
                .pacienteId(consulta.getPaciente().getId())
                .pacienteNome(consulta.getPaciente().getNome())
                .medicoId(consulta.getMedico().getId())
                .medicoNome(consulta.getMedico().getNome())
                .dataHora(consulta.getDataConsulta())
                .status(consulta.getStatus().toString())
                .observacoes(consulta.getObservacoes())
                .build();
    }
}