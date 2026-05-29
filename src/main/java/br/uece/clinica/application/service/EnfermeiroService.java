package br.uece.clinica.application.service;

import br.uece.clinica.domain.model.Enfermeiro;
import br.uece.clinica.domain.repository.EnfermeiroRepository;
import br.uece.clinica.domain.repository.TriagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EnfermeiroService {
    private final EnfermeiroRepository enfermeiroRepository;
    private final TriagemRepository triagemRepository;

    public Enfermeiro criarEnfermeiro(String nome, String coren, String telefone, String email,
                                      String especialidade, Enfermeiro.Turno turno) {
        if (enfermeiroRepository.findByCoren(coren).isPresent()) {
            throw new RuntimeException("COREN já cadastrado: " + coren);
        }

        Enfermeiro enfermeiro = new Enfermeiro(nome, coren, telefone, email, especialidade, turno);
        return enfermeiroRepository.save(enfermeiro);
    }

    public Enfermeiro atualizarEnfermeiro(UUID id, String nome, String telefone, String email,
                                          String especialidade, Enfermeiro.Turno turno) {
        Enfermeiro enfermeiro = enfermeiroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enfermeiro não encontrado"));

        enfermeiro.setNome(nome);
        enfermeiro.setTelefone(telefone);
        enfermeiro.setEmail(email);
        enfermeiro.setEspecialidade(especialidade);
        enfermeiro.setTurno(turno);

        return enfermeiroRepository.save(enfermeiro);
    }

    @Transactional(readOnly = true)
    public Enfermeiro obterPorId(UUID id) {
        return enfermeiroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enfermeiro não encontrado"));
    }

    @Transactional(readOnly = true)
    public Enfermeiro obterPorCoren(String coren) {
        return enfermeiroRepository.findByCoren(coren)
                .orElseThrow(() -> new RuntimeException("Enfermeiro não encontrado: " + coren));
    }

    @Transactional(readOnly = true)
    public List<Enfermeiro> listarTodos() {
        return enfermeiroRepository.findAllAtivos();
    }

    @Transactional(readOnly = true)
    public List<Enfermeiro> listarPorTurno(Enfermeiro.Turno turno) {
        return enfermeiroRepository.findByTurno(turno);
    }

    @Transactional(readOnly = true)
    public List<Enfermeiro> listarPorEspecialidade(String especialidade) {
        return enfermeiroRepository.findByEspecialidade(especialidade);
    }

    @Transactional(readOnly = true)
    public List<Enfermeiro> listarMaisExperientes() {
        return enfermeiroRepository.findMaisExperientes();
    }

    @Transactional(readOnly = true)
    public List<Enfermeiro> listarEmTurno() {
        return enfermeiroRepository.findEnfermeiroEmTurno();
    }

    public void desativarEnfermeiro(UUID id) {
        Enfermeiro enfermeiro = enfermeiroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enfermeiro não encontrado"));

        enfermeiro.setAtivo(false);
        enfermeiroRepository.save(enfermeiro);
    }

    public void ativarEnfermeiro(UUID id) {
        Enfermeiro enfermeiro = enfermeiroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enfermeiro não encontrado"));

        enfermeiro.setAtivo(true);
        enfermeiroRepository.save(enfermeiro);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obterEstatisticasEnfermeiro(UUID enfermeiroId) {
        Enfermeiro enfermeiro = enfermeiroRepository.findById(enfermeiroId)
                .orElseThrow(() -> new RuntimeException("Enfermeiro não encontrado"));

        Map<String, Object> stats = new HashMap<>();
        stats.put("nome", enfermeiro.getNome());
        stats.put("coren", enfermeiro.getCoren());
        stats.put("especialidade", enfermeiro.getEspecialidade());
        stats.put("turno", enfermeiro.getTurno().getDescricao());
        stats.put("totalTriagens", enfermeiro.getTotalTriagensRealizadas());
        stats.put("triagensHoje", triagemRepository.contarTriagensRealizadasHoje(enfermeiro));
        stats.put("ultimoAcesso", enfermeiro.getUltimoAcesso());
        stats.put("ativo", enfermeiro.isAtivo());

        return stats;
    }

    @Transactional(readOnly = true)
    public Map<String, Integer> obterEstatisticasGerais() {
        List<Enfermeiro> enfermeiros = enfermeiroRepository.findAllAtivos();

        Map<String, Integer> stats = new HashMap<>();
        stats.put("totalEnfermeiros", (int) enfermeiroRepository.count());
        stats.put("enfermeiroAtivos", enfermeiros.size());
        stats.put("enfermeiroEmTurno", enfermeiroRepository.findEnfermeiroEmTurno().size());

        int totalTriagens = enfermeiros.stream()
                .mapToInt(Enfermeiro::getTotalTriagensRealizadas)
                .sum();
        stats.put("totalTriagensRealizadas", totalTriagens);

        return stats;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarTodasAsEstatisticas() {
        return enfermeiroRepository.findAllAtivos().stream()
                .map(e -> {
                    Map<String, Object> m = new HashMap<>();
                    m.put("id", e.getId());
                    m.put("nome", e.getNome());
                    m.put("coren", e.getCoren());
                    m.put("especialidade", e.getEspecialidade());
                    m.put("turno", e.getTurno().getDescricao());
                    m.put("totalTriagens", e.getTotalTriagensRealizadas());
                    m.put("triagensHoje", triagemRepository.contarTriagensRealizadasHoje(e));
                    return m;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Enfermeiro> buscarPorNome(String nome) {
        return enfermeiroRepository.findAllAtivos().stream()
                .filter(e -> e.getNome().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void validarEnfermeiro(UUID id) {
        Enfermeiro enfermeiro = enfermeiroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enfermeiro não encontrado"));

        if (!enfermeiro.isAtivo()) {
            throw new RuntimeException("Enfermeiro inativo");
        }

        if (!enfermeiro.podeRealizarTriagem()) {
            throw new RuntimeException("Enfermeiro não está em turno");
        }
    }
}