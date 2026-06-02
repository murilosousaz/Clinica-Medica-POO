package br.uece.clinica.application.service;

import br.uece.clinica.domain.exception.PacienteDuplicadoException;
import br.uece.clinica.domain.model.Paciente;
import br.uece.clinica.domain.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public Paciente cadastrar(Paciente paciente) {

        pacienteRepository.findByCpf(paciente.getCpf())
                .ifPresent(p -> {
                    throw new PacienteDuplicadoException(
                            "Paciente já cadastrado com CPF: " + paciente.getCpf()
                    );
                });

        return pacienteRepository.save(paciente);
    }

    @Transactional(readOnly = true)
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAllAtivos();
    }

    @Transactional(readOnly = true)
    public Paciente buscarPorId(UUID id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Paciente> buscarPorNome(String nome) {
        return pacienteRepository.searchPorNome(nome);
    }

    public Paciente atualizar(UUID id, Paciente dados) {

        Paciente paciente = buscarPorId(id);

        paciente.setNome(dados.getNome());
        paciente.setTelefone(dados.getTelefone());
        paciente.setEmail(dados.getEmail());

        return pacienteRepository.save(paciente);
    }

    public void excluir(UUID id) {

        Paciente paciente = buscarPorId(id);

        paciente.setAtivo(false);

        pacienteRepository.save(paciente);
    }
}