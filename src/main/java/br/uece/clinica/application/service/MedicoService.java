package br.uece.clinica.application.service;

import br.uece.clinica.domain.model.*;
import br.uece.clinica.domain.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicoService {
    private final MedicoRepository medicoRepository;

    public Medico criarMedico(String nome, String crm, String especialidade,
                              String telefone, String email, BigDecimal valor,
                              Set<String> planos) {
        if (medicoRepository.findByCrm(crm).isPresent()) {
            throw new RuntimeException("CRM já cadastrado: " + crm);
        }

        Medico medico = null;
        switch (especialidade.toUpperCase()) {
            case "CARDIOLOGISTA":
                medico = new Cardiologista(nome, crm, telefone, email);
                break;
            case "PEDIATRA":
                medico = new Pediatra(nome, crm, telefone, email);
                break;
            case "DERMATOLOGISTA":
                medico = new Dermatologista(nome, crm, telefone, email);
                break;
            default:
                throw new RuntimeException("Especialidade inválida: " + especialidade);
        }

        if (planos != null) {
            medico.adicionarPlanosAtendidos(planos.toArray(new String[0]));
        }

        return medicoRepository.save(medico);
    }

    @Transactional(readOnly = true)
    public Medico obterPorId(UUID id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
    }

    @Transactional(readOnly = true)
    public Medico obterPorCrm(String crm) {
        return medicoRepository.findByCrm(crm)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado: " + crm));
    }

    @Transactional(readOnly = true)
    public List<Medico> listarTodos() {
        return medicoRepository.findByEspecialidadeAtivo("CARDIOLOGISTA").stream()
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<Medico> listarPorEspecialidade(String especialidade) {
        return medicoRepository.findByEspecialidadeAtivo(especialidade.toUpperCase());
    }

    @Transactional(readOnly = true)
    public List<Medico> listarPorPlano(String nomePlano) {
        return medicoRepository.findByPlanosAtendidosContains(nomePlano);
    }

    @Transactional(readOnly = true)
    public List<Medico> listarMesBemAvaliados() {
        return medicoRepository.findMesBemAvaliados();
    }

    @Transactional(readOnly = true)
    public List<Medico> buscarPorNome(String nome) {
        return medicoRepository.findByEspecialidadeAtivo("CARDIOLOGISTA").stream()
                .filter(m -> m.getNome().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Medico atualizarMedico(UUID id, String nome, String telefone, String email,
                                  BigDecimal valor, Set<String> planos) {
        Medico medico = obterPorId(id);
        medico.setNome(nome);
        medico.setTelefone(telefone);
        medico.setEmail(email);
        medico.setValorConsultaParticular(valor);
        if (planos != null) {
            medico.setPlanosAtendidos(planos);
        }
        return medicoRepository.save(medico);
    }

    public void desativarMedico(UUID id) {
        Medico medico = obterPorId(id);
        medico.setAtivo(false);
        medicoRepository.save(medico);
    }

    public void ativarMedico(UUID id) {
        Medico medico = obterPorId(id);
        medico.setAtivo(true);
        medicoRepository.save(medico);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obterEstatisticas(UUID id) {
        Medico medico = obterPorId(id);
        Map<String, Object> stats = new HashMap<>();
        stats.put("nome", medico.getNome());
        stats.put("crm", medico.getCrm());
        stats.put("especialidade", medico.getEspecialidade());
        stats.put("mediaAvaliacoes", medico.getMediaAvaliacoes());
        stats.put("totalAvaliacoes", medico.getAvaliacoes().size());
        stats.put("maxPacientesPorDia", medico.getMaxPacientesPorDia());
        return stats;
    }
}