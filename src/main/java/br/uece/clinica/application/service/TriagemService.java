package br.uece.clinica.application.service;

import br.uece.clinica.domain.model.*;
import br.uece.clinica.domain.repository.EnfermeiroRepository;
import br.uece.clinica.domain.repository.PacienteRepository;
import br.uece.clinica.domain.repository.TriagemRepository;
import br.uece.clinica.domain.valueobject.PrioridadeSUS;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
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

    public Triagem registrarTriagem(UUID pacienteId, UUID enfermeiroId, PrioridadeSUS prioridade,
                                    String queijaPrincipal, String pressaoArterial, Integer frequenciaCardiaca,
                                    Double temperatura, Integer frequenciaRespiratoria, Double peso, Double altura) {

        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        Enfermeiro enfermeiro = enfermeiroRepository.findById(enfermeiroId)
                .orElseThrow(() -> new RuntimeException("Enfermeiro não encontrado"));

        if (!enfermeiro.podeRealizarTriagem()) {
            throw new RuntimeException("Enfermeiro não está em turno ou inativo");
        }

        Triagem triagem = new Triagem(paciente, enfermeiro, prioridade, queijaPrincipal);
        triagem.setPressaoArterial(pressaoArterial);
        triagem.setFrequenciaCardiaca(frequenciaCardiaca);
        triagem.setTemperatura(temperatura);
        triagem.setFrequenciaRespiratoria(frequenciaRespiratoria);
        triagem.setPeso(peso);
        triagem.setAltura(altura);

        Triagem saved = triagemRepository.save(triagem);
        enfermeiro.registrarTriagem(saved);
        enfermeiroRepository.save(enfermeiro);

        filaTriagem.offer(saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public Triagem obterProximaDaFila() {
        return filaTriagem.peek();
    }

    public Triagem chamarProximoPaciente() {
        Triagem triagem = filaTriagem.poll();
        if (triagem == null) {
            throw new RuntimeException("Fila vazia");
        }
        return triagem;
    }

    @Transactional(readOnly = true)
    public List<Triagem> listarTriagensDoiaDia() {
        return triagemRepository.findTriagensDoiaDia(LocalDate.now());
    }

    @Transactional(readOnly = true)
    public List<Triagem> listarTriagensEmergenciaHoje() {
        return triagemRepository.findTriagensEmergenciaHoje();
    }

    @Transactional(readOnly = true)
    public Map<PrioridadeSUS, List<Triagem>> agruparTriagensHojePorPrioridade() {
        List<Triagem> triagensHoje = listarTriagensDoiaDia();
        return triagensHoje.stream()
                .collect(Collectors.groupingBy(Triagem::getPrioridade));
    }

    @Transactional(readOnly = true)
    public int contarTriagensEnfermeiro(UUID enfermeiroId) {
        Enfermeiro enfermeiro = enfermeiroRepository.findById(enfermeiroId)
                .orElseThrow(() -> new RuntimeException("Enfermeiro não encontrado"));
        return triagemRepository.contarTriagensRealizadasHoje(enfermeiro);
    }

    @Transactional(readOnly = true)
    public List<Triagem> buscarTriagensEnfermeiro(UUID enfermeiroId) {
        Enfermeiro enfermeiro = enfermeiroRepository.findById(enfermeiroId)
                .orElseThrow(() -> new RuntimeException("Enfermeiro não encontrado"));
        return triagemRepository.findByEnfermeiro(enfermeiro);
    }

    public void atualizarTriagem(UUID triagemId, String pressaoArterial, Integer frequenciaCardiaca,
                                 Double temperatura, Integer frequenciaRespiratoria, Double peso, Double altura) {
        Triagem triagem = triagemRepository.findById(triagemId)
                .orElseThrow(() -> new RuntimeException("Triagem não encontrada"));

        triagem.setPressaoArterial(pressaoArterial);
        triagem.setFrequenciaCardiaca(frequenciaCardiaca);
        triagem.setTemperatura(temperatura);
        triagem.setFrequenciaRespiratoria(frequenciaRespiratoria);
        triagem.setPeso(peso);
        triagem.setAltura(altura);

        triagemRepository.save(triagem);
    }

    @Transactional(readOnly = true)
    public int tamanhoFilaPorPrioridade(PrioridadeSUS prioridade) {
        return (int) filaTriagem.stream()
                .filter(t -> t.getPrioridade() == prioridade)
                .count();
    }

    @Transactional(readOnly = true)
    public int tamanhoFila() {
        return filaTriagem.size();
    }
}