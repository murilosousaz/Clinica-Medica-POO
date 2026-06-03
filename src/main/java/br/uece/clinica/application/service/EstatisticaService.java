package br.uece.clinica.application.service;

import br.uece.clinica.domain.model.Avaliacao;
import br.uece.clinica.domain.model.Consulta;
import br.uece.clinica.domain.model.Medico;
import br.uece.clinica.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstatisticaService {

    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final ConsultaRepository consultaRepository;
    private final ContaRepository contaRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    public Map<String, Object> dashboard() {
        Map<String, Object> dados = new HashMap<>();

        var medicos = medicoRepository.findAll();
        var consultas = consultaRepository.findAll();
        var avaliacoes = avaliacaoRepository.findAll();

        dados.put("pacientes", pacienteRepository.count());
        dados.put("medicos", medicoRepository.count());
        dados.put("consultas", consultaRepository.count());
        dados.put("consultasRealizadas", consultas.stream().filter(c -> c.getStatus() == Consulta.StatusConsulta.REALIZADA).count());
        dados.put("contasPendentes", contaRepository.contarContasPendentes());
        dados.put("mediaGeralAvaliacoes", avaliacoes.stream().map(Avaliacao::getEstrelas).filter(e -> e != null).mapToInt(Integer::intValue).average().orElse(0.0));

        dados.put("medicoMaisBemAvaliado", medicos.stream()
                .max(Comparator.comparingDouble(Medico::getMediaAvaliacoes))
                .map(m -> Map.of("id", m.getId(), "nome", m.getNome(), "media", m.getMediaAvaliacoes()))
                .orElse(null));

        dados.put("especialidadeMaisProcurada", consultas.stream()
                .collect(Collectors.groupingBy(c -> c.getMedico().getEspecialidade(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(e -> Map.of("especialidade", e.getKey(), "consultas", e.getValue()))
                .orElse(null));

        dados.put("consultasPorMedico", consultas.stream()
                .collect(Collectors.groupingBy(c -> c.getMedico().getNome(), Collectors.counting())));

        return dados;
    }
}
