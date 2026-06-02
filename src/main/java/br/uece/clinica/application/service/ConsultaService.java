package br.uece.clinica.application.service;

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

@Service
@RequiredArgsConstructor
@Transactional
public class ConsultaService {

    private final ConsultaRepository consultaRepository;
    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;

    public Consulta agendar(Consulta consulta) {

        List<Consulta> consultas =
                consultaRepository.findConsultasDoMedicoNoDia(
                        consulta.getMedico(),
                        consulta.getDataConsulta()
                );

        if (consultas.size() >= 20) {
            throw new AgendaLotadaException("Agenda lotada");
        }

        return consultaRepository.save(consulta);
    }

    @Transactional(readOnly = true)
    public Consulta buscarPorId(UUID id) {
        return consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Consulta> listarTodas() {
        return consultaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Consulta> listarPorPaciente(UUID pacienteId) {

        Paciente paciente =
                pacienteRepository.findById(pacienteId)
                        .orElseThrow();

        return consultaRepository.findByPaciente(paciente);
    }

    @Transactional(readOnly = true)
    public List<Consulta> listarPorMedico(UUID medicoId) {

        Medico medico =
                medicoRepository.findById(medicoId)
                        .orElseThrow();

        return consultaRepository.findByMedico(medico);
    }

    public Consulta cancelar(UUID id) {

        Consulta consulta = buscarPorId(id);

        consulta.cancelar();

        return consultaRepository.save(consulta);
    }

    @Transactional(readOnly = true)
    public List<Consulta> consultasHoje() {
        return consultaRepository.findConsultasHojeAgendadas();
    }

    @Transactional(readOnly = true)
    public List<Consulta> consultasPeriodo(
            LocalDate inicio,
            LocalDate fim) {

        return consultaRepository.findConsultasEmPeriodo(
                inicio,
                fim
        );
    }
}