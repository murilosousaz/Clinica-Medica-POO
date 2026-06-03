package br.uece.clinica.application.service;

import br.uece.clinica.application.dto.TriagemRequest;
import br.uece.clinica.application.dto.TriagemResponse;
import br.uece.clinica.application.mapper.TriagemMapper;
import br.uece.clinica.domain.model.Enfermeiro;
import br.uece.clinica.domain.model.Paciente;
import br.uece.clinica.domain.model.Triagem;
import br.uece.clinica.domain.repository.EnfermeiroRepository;
import br.uece.clinica.domain.repository.PacienteRepository;
import br.uece.clinica.domain.repository.TriagemRepository;
import br.uece.clinica.domain.valueobject.PrioridadeSUS;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TriagemService {
    private final TriagemRepository triagemRepository;
    private final PacienteRepository pacienteRepository;
    private final EnfermeiroRepository enfermeiroRepository;

    private final PriorityQueue<Triagem> filaTriagem = new PriorityQueue<>(
            Comparator.comparingInt((Triagem t) -> t.getPrioridade().ordinal())
    );

    public TriagemResponse registrar(TriagemRequest request) {
        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        Enfermeiro enfermeiro = enfermeiroRepository.findById(request.getEnfermeiroId())
                .orElseThrow(() -> new RuntimeException("Enfermeiro não encontrado"));

        if (!enfermeiro.podeRealizarTriagem()) {
            throw new RuntimeException("Enfermeiro não está em turno ou inativo");
        }

        Triagem triagem = TriagemMapper.toEntity(request, paciente, enfermeiro);
        Triagem salva = triagemRepository.save(triagem);
        enfermeiro.registrarTriagem(salva);
        enfermeiroRepository.save(enfermeiro);
        filaTriagem.offer(salva);
        return TriagemMapper.toResponse(salva);
    }

    @Transactional(readOnly = true)
    public TriagemResponse obterProximaDaFila() {
        Triagem triagem = filaTriagem.peek();
        if (triagem == null) {
            throw new RuntimeException("Fila vazia");
        }
        return TriagemMapper.toResponse(triagem);
    }

    public TriagemResponse chamarProximoPaciente() {
        Triagem triagem = filaTriagem.poll();
        if (triagem == null) {
            throw new RuntimeException("Fila vazia");
        }
        return TriagemMapper.toResponse(triagem);
    }

    @Transactional(readOnly = true)
    public List<TriagemResponse> listarTodas() {
        return triagemRepository.findAll().stream()
                .map(TriagemMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TriagemResponse buscarPorId(UUID id) {
        Triagem triagem = triagemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Triagem não encontrada"));
        return TriagemMapper.toResponse(triagem);
    }

    @Transactional(readOnly = true)
    public List<TriagemResponse> listarTriagensDoDia() {
        return triagemRepository.findTriagensDoDia(LocalDate.now()).stream()
                .map(TriagemMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TriagemResponse> listarTriagensEmergenciaHoje() {
        return triagemRepository.findTriagensEmergenciaHoje().stream()
                .map(TriagemMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<PrioridadeSUS, List<Triagem>> agruparTriagensHojePorPrioridade() {
        return triagemRepository.findTriagensDoDia(LocalDate.now()).stream()
                .collect(Collectors.groupingBy(Triagem::getPrioridade));
    }

    @Transactional(readOnly = true)
    public int contarTriagensEnfermeiro(UUID enfermeiroId) {
        Enfermeiro enfermeiro = enfermeiroRepository.findById(enfermeiroId)
                .orElseThrow(() -> new RuntimeException("Enfermeiro não encontrado"));
        return triagemRepository.contarTriagensRealizadasHoje(enfermeiro);
    }

    @Transactional(readOnly = true)
    public int tamanhoFila() {
        return filaTriagem.size();
    }
}
