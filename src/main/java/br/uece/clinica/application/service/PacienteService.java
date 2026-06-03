package br.uece.clinica.application.service;

import br.uece.clinica.domain.model.Paciente;
import br.uece.clinica.domain.repository.PacienteRepository;
import br.uece.clinica.domain.valueobject.PlanoSaude;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PacienteService {
    private final PacienteRepository pacienteRepository;

    public Paciente criarPaciente(String nome, Integer idade, String cpf, String telefone,
                                  String email, String nomePlano, String numeroCarnetizacao, Boolean planoAtivo) {
        if (cpf != null && pacienteRepository.findByCpf(cpf).isPresent()) {
            throw new RuntimeException("CPF já cadastrado: " + cpf);
        }

        Paciente paciente = new Paciente(nome, idade, cpf, telefone, email);

        if (nomePlano != null) {
            PlanoSaude plano = new PlanoSaude(nomePlano, numeroCarnetizacao, planoAtivo != null ? planoAtivo : false);
            paciente.atualizarPlanoSaude(plano);
        } else {
            paciente.atualizarPlanoSaude(PlanoSaude.naoPossui());
        }

        return pacienteRepository.save(paciente);
    }

    @Transactional(readOnly = true)
    public Paciente obterPorId(UUID id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
    }

    @Transactional(readOnly = true)
    public Paciente obterPorCpf(String cpf) {
        return pacienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado: " + cpf));
    }

    @Transactional(readOnly = true)
    public Paciente obterPorEmail(String email) {
        return pacienteRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado: " + email));
    }

    @Transactional(readOnly = true)
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAllAtivos();
    }

    @Transactional(readOnly = true)
    public List<Paciente> listarPorPlano(String nomePlano) {
        return pacienteRepository.findByPlanosSaude(nomePlano);
    }

    @Transactional(readOnly = true)
    public List<Paciente> buscarPorNome(String nome) {
        return pacienteRepository.searchPorNome(nome);
    }

    public Paciente atualizarPaciente(UUID id, String nome, Integer idade, String telefone,
                                      String email, String nomePlano, String numeroCarnetizacao, Boolean planoAtivo) {
        Paciente paciente = obterPorId(id);
        paciente.setNome(nome);
        paciente.setIdade(idade);
        paciente.setTelefone(telefone);
        paciente.setEmail(email);

        if (nomePlano != null) {
            PlanoSaude plano = new PlanoSaude(nomePlano, numeroCarnetizacao, planoAtivo != null ? planoAtivo : false);
            paciente.atualizarPlanoSaude(plano);
        }

        return pacienteRepository.save(paciente);
    }

    public void desativarPaciente(UUID id) {
        Paciente paciente = obterPorId(id);
        paciente.setAtivo(false);
        pacienteRepository.save(paciente);
    }

    public void ativarPaciente(UUID id) {
        Paciente paciente = obterPorId(id);
        paciente.setAtivo(true);
        pacienteRepository.save(paciente);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obterHistoricoConsultas(UUID id) {
        Paciente paciente = obterPorId(id);
        Map<String, Object> historico = new HashMap<>();
        historico.put("paciente", paciente.getNome());
        historico.put("totalConsultas", paciente.getHistoricoConsultas().size());
        historico.put("consultas", paciente.getHistoricoConsultas());
        return historico;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obterContas(UUID id) {
        Paciente paciente = obterPorId(id);
        Map<String, Object> contas = new HashMap<>();
        contas.put("paciente", paciente.getNome());
        contas.put("totalContas", paciente.getContas().size());
        contas.put("contas", paciente.getContas());
        return contas;
    }
}