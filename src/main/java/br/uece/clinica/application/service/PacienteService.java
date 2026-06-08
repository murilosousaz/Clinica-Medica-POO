package br.uece.clinica.application.service;

import br.uece.clinica.application.dto.CreatePacienteRequest;
import br.uece.clinica.application.dto.PacienteResponse;
import br.uece.clinica.application.mapper.PacienteMapper;
import br.uece.clinica.domain.model.Paciente;
import br.uece.clinica.domain.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PacienteService {
    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;

    public PacienteResponse cadastrar(CreatePacienteRequest request) {
        request.setCpf(normalizarCpf(request.getCpf()));
        validarCpfUnico(request.getCpf(), null);
        Paciente paciente = PacienteMapper.toEntity(request);
        paciente.setSenhaHash(criptografarSenha(request.getSenha(), request.getCpf()));
        return PacienteMapper.toResponse(pacienteRepository.save(paciente));
    }

    public PacienteResponse atualizar(UUID id, CreatePacienteRequest request) {
        request.setCpf(normalizarCpf(request.getCpf()));
        Paciente paciente = obterEntidadePorId(id);
        validarCpfUnico(request.getCpf(), id);
        paciente.setNome(request.getNome());
        paciente.setIdade(request.getIdade());
        paciente.setCpf(request.getCpf());
        paciente.setTelefone(request.getTelefone());
        paciente.setEmail(request.getEmail());
        paciente.atualizarPlanoSaude(request.getPlanoSaude());
        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            paciente.setSenhaHash(criptografarSenha(request.getSenha(), request.getCpf()));
        }
        return PacienteMapper.toResponse(pacienteRepository.save(paciente));
    }

    public void excluir(UUID id) {
        Paciente paciente = obterEntidadePorId(id);
        paciente.setAtivo(false);
        pacienteRepository.save(paciente);
    }

    @Transactional(readOnly = true)
    public PacienteResponse buscarPorId(UUID id) {
        return PacienteMapper.toResponse(obterEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public List<PacienteResponse> listarTodos() {
        return pacienteRepository.findAllAtivos().stream()
                .map(PacienteMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PacienteResponse> buscarPorNome(String nome) {
        return pacienteRepository.searchPorNome(nome).stream()
                .map(PacienteMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Paciente obterEntidadePorId(UUID id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
    }

    @Transactional(readOnly = true)
    public Paciente obterPorCpf(String cpf) {
        return pacienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado: " + cpf));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obterHistoricoConsultas(UUID id) {
        Paciente paciente = obterEntidadePorId(id);
        Map<String, Object> historico = new HashMap<>();
        historico.put("paciente", paciente.getNome());
        historico.put("totalConsultas", paciente.getHistoricoConsultas().size());
        historico.put("consultas", paciente.getHistoricoConsultas());
        return historico;
    }

    private String normalizarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF é obrigatório");
        }
        return cpf.replaceAll("\\D", "");
    }

    private String criptografarSenha(String senha, String cpf) {
        String senhaEfetiva = senha;
        if (senhaEfetiva == null || senhaEfetiva.isBlank()) {
            senhaEfetiva = cpf;
        }
        if (senhaEfetiva.length() < 6) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres");
        }
        return passwordEncoder.encode(senhaEfetiva);
    }

    private void validarCpfUnico(String cpf, UUID idAtual) {
        pacienteRepository.findByCpf(cpf).ifPresent(p -> {
            if (idAtual == null || !p.getId().equals(idAtual)) {
                throw new RuntimeException("CPF já cadastrado: " + cpf);
            }
        });
    }
}
