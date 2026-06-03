package br.uece.clinica.application.service;

import br.uece.clinica.application.dto.ConsultaRequest;
import br.uece.clinica.application.dto.ConsultaResponse;
import br.uece.clinica.application.dto.ListaEsperaResponse;
import br.uece.clinica.application.dto.RealizarConsultaRequest;
import br.uece.clinica.application.mapper.ConsultaMapper;
import br.uece.clinica.domain.model.Consulta;
import br.uece.clinica.domain.model.Conta;
import br.uece.clinica.domain.model.ListaEspera;
import br.uece.clinica.domain.model.Medico;
import br.uece.clinica.domain.model.Paciente;
import br.uece.clinica.domain.repository.ConsultaRepository;
import br.uece.clinica.domain.repository.ContaRepository;
import br.uece.clinica.domain.repository.ListaEsperaRepository;
import br.uece.clinica.domain.repository.MedicoRepository;
import br.uece.clinica.domain.repository.PacienteRepository;
import br.uece.clinica.domain.valueobject.Diagnostico;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final ListaEsperaRepository listaEsperaRepository;
    private final ContaRepository contaRepository;

    public ConsultaResponse agendar(ConsultaRequest request) {
        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        Medico medico = medicoRepository.findById(request.getMedicoId())
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));

        LocalDate dia = request.getDataHora().toLocalDate();
        long consultasAgendadas = consultaRepository.findConsultasDoMedicoNoDia(medico, dia).stream()
                .filter(c -> c.getStatus() == Consulta.StatusConsulta.AGENDADA)
                .count();

        if (consultasAgendadas >= medico.getMaxPacientesPorDia()) {
            ListaEspera item = new ListaEspera(
                    paciente,
                    medico,
                    dia,
                    request.getDataHora().toLocalTime().toString(),
                    request.getObservacoes()
            );
            ListaEspera salvo = listaEsperaRepository.save(item);
            return ConsultaResponse.builder()
                    .id(salvo.getId())
                    .pacienteId(paciente.getId())
                    .pacienteNome(paciente.getNome())
                    .medicoId(medico.getId())
                    .medicoNome(medico.getNome())
                    .dataHora(request.getDataHora())
                    .status("LISTA_ESPERA")
                    .observacoes("Agenda lotada. Paciente colocado na lista de espera em ordem de entrada.")
                    .build();
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
        return consultaRepository.findByPaciente(paciente).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponse> prontuarioPaciente(UUID pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        return consultaRepository.findByPaciente(paciente).stream()
                .filter(c -> c.getStatus() == Consulta.StatusConsulta.REALIZADA)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponse> listarPorMedico(UUID medicoId) {
        Medico medico = medicoRepository.findById(medicoId)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
        return consultaRepository.findByMedico(medico).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ConsultaResponse cancelar(UUID id) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        consulta.cancelar();
        Consulta consultaSalva = consultaRepository.save(consulta);
        promoverPrimeiroDaListaDeEspera(consultaSalva);
        return toResponse(consultaSalva);
    }

    public ConsultaResponse realizar(UUID id, RealizarConsultaRequest request) {
        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        Diagnostico diagnostico = new Diagnostico(
                request.getSintomas(),
                request.getDiagnostico(),
                request.getTratamentoSugerido(),
                request.getObservacoesGerais()
        );

        consulta.realizarConsulta(diagnostico, request.getMedicamentos(), request.getExamesSolicitados());
        consulta.setObservacoes(request.getObservacoesGerais());
        Consulta salva = consultaRepository.save(consulta);

        if (salva.getValorPago() != null && salva.getValorPago().compareTo(BigDecimal.ZERO) > 0) {
            Conta conta = new Conta(
                    salva.getPaciente(),
                    salva,
                    salva.getValorPago(),
                    "Consulta particular realizada em " + salva.getDataConsulta(),
                    LocalDate.now().plusDays(30)
            );
            contaRepository.save(conta);
        }

        return toResponse(salva);
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponse> consultasHoje() {
        return consultaRepository.findConsultasHojeAgendadas().stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConsultaResponse> consultasPeriodo(LocalDate inicio, LocalDate fim) {
        return consultaRepository.findConsultasEmPeriodo(inicio, fim).stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ListaEsperaResponse> listarFilaEspera() {
        return listaEsperaRepository.findByStatusOrderByDataConsultaAscDataCriacaoAsc(ListaEspera.StatusListaEspera.ESPERANDO).stream().map(this::toListaResponse).toList();
    }

    private void promoverPrimeiroDaListaDeEspera(Consulta consultaCancelada) {
        List<ListaEspera> fila = listaEsperaRepository.findByMedicoAndDataConsultaAndStatusOrderByDataCriacaoAsc(
                consultaCancelada.getMedico(),
                consultaCancelada.getDataConsulta(),
                ListaEspera.StatusListaEspera.ESPERANDO
        );
        if (fila.isEmpty()) {
            return;
        }

        ListaEspera primeiro = fila.get(0);
        Consulta novaConsulta = new Consulta(
                primeiro.getPaciente(),
                primeiro.getMedico(),
                consultaCancelada.getDataHora(),
                primeiro.getObservacoes()
        );
        consultaRepository.save(novaConsulta);
        primeiro.promover(consultaCancelada.getHorario());
        listaEsperaRepository.save(primeiro);
    }

    private ListaEsperaResponse toListaResponse(ListaEspera item) {
        return ListaEsperaResponse.builder()
                .id(item.getId())
                .pacienteId(item.getPaciente().getId())
                .pacienteNome(item.getPaciente().getNome())
                .medicoId(item.getMedico().getId())
                .medicoNome(item.getMedico().getNome())
                .dataConsulta(item.getDataConsulta())
                .horarioDesejado(item.getHorarioDesejado())
                .status(item.getStatus().name())
                .notificacao(item.getNotificacao())
                .build();
    }

    private ConsultaResponse toResponse(Consulta consulta) {
        String sintomas = null;
        String diagnostico = null;
        String tratamento = null;
        String observacoesDiagnostico = null;
        if (consulta.getDiagnostico() != null) {
            sintomas = consulta.getDiagnostico().getSintomas();
            diagnostico = consulta.getDiagnostico().getDiagnosticoTexto();
            tratamento = consulta.getDiagnostico().getTratamentoSugerido();
            observacoesDiagnostico = consulta.getDiagnostico().getObservacoes();
        }

        return ConsultaResponse.builder()
                .id(consulta.getId())
                .pacienteId(consulta.getPaciente().getId())
                .pacienteNome(consulta.getPaciente().getNome())
                .medicoId(consulta.getMedico().getId())
                .medicoNome(consulta.getMedico().getNome())
                .dataHora(consulta.getDataHora())
                .status(consulta.getStatus().toString())
                .observacoes(consulta.getObservacoes() != null ? consulta.getObservacoes() : observacoesDiagnostico)
                .sintomas(sintomas)
                .diagnostico(diagnostico)
                .tratamentoSugerido(tratamento)
                .medicamentos(consulta.getReceita())
                .examesSolicitados(consulta.getExamesSolicitados())
                .valorPago(consulta.getValorPago())
                .avaliacaoEstrelas(consulta.getAvaliacao() != null ? consulta.getAvaliacao().getEstrelas() : null)
                .avaliacaoTexto(consulta.getAvaliacao() != null ? consulta.getAvaliacao().getTexto() : null)
                .build();
    }
}
