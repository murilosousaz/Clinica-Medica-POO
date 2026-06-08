package br.uece.clinica.application.service;

import br.uece.clinica.application.dto.CreateMedicoRequest;
import br.uece.clinica.application.dto.MedicoResponse;
import br.uece.clinica.application.mapper.MedicoMapper;
import br.uece.clinica.domain.model.Medico;
import br.uece.clinica.domain.repository.MedicoRepository;
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
public class MedicoService {
    private final MedicoRepository medicoRepository;
    private final PasswordEncoder passwordEncoder;

    public MedicoResponse salvar(CreateMedicoRequest request) {
        request.setCpf(normalizarCpf(request.getCpf()));
        validarCrmUnico(request.getCrm(), null);
        validarCpfUnico(request.getCpf(), null);
        Medico medico = MedicoMapper.toEntity(request);
        medico.setSenhaHash(criptografarSenha(request.getSenha(), request.getCpf()));
        return MedicoMapper.toResponse(medicoRepository.save(medico));
    }

    public MedicoResponse atualizar(UUID id, CreateMedicoRequest request) {
        request.setCpf(normalizarCpf(request.getCpf()));
        Medico medico = obterEntidadePorId(id);
        validarCrmUnico(request.getCrm(), id);
        validarCpfUnico(request.getCpf(), id);
        MedicoMapper.updateEntity(medico, request);
        if (request.getSenha() != null && !request.getSenha().isBlank()) {
            medico.setSenhaHash(criptografarSenha(request.getSenha(), request.getCpf()));
        }
        return MedicoMapper.toResponse(medicoRepository.save(medico));
    }

    public void desativar(UUID id) {
        Medico medico = obterEntidadePorId(id);
        medico.setAtivo(false);
        medicoRepository.save(medico);
    }

    @Transactional(readOnly = true)
    public MedicoResponse buscarPorId(UUID id) {
        return MedicoMapper.toResponse(obterEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public List<MedicoResponse> listarTodos() {
        return medicoRepository.findAll().stream()
                .filter(Medico::isAtivo)
                .map(MedicoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MedicoResponse> buscarPorEspecialidade(String especialidade) {
        return medicoRepository.findByEspecialidadeAtivo(normalizarEspecialidade(especialidade)).stream()
                .map(MedicoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MedicoResponse> rankingAvaliacoes() {
        return medicoRepository.findMaisBemAvaliados().stream()
                .map(MedicoMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Medico obterEntidadePorId(UUID id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obterEstatisticas(UUID id) {
        Medico medico = obterEntidadePorId(id);
        Map<String, Object> stats = new HashMap<>();
        stats.put("nome", medico.getNome());
        stats.put("crm", medico.getCrm());
        stats.put("especialidade", medico.getEspecialidade());
        stats.put("mediaAvaliacoes", medico.getMediaAvaliacoes());
        stats.put("totalAvaliacoes", medico.getAvaliacoes().size());
        stats.put("maxPacientesPorDia", medico.getMaxPacientesPorDia());
        return stats;
    }

    private String normalizarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF do médico é obrigatório para autenticação");
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

    private void validarCrmUnico(String crm, UUID idAtual) {
        medicoRepository.findByCrm(crm).ifPresent(m -> {
            if (idAtual == null || !m.getId().equals(idAtual)) {
                throw new RuntimeException("CRM já cadastrado: " + crm);
            }
        });
    }



    private void validarCpfUnico(String cpf, UUID idAtual) {
        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF do médico é obrigatório para autenticação");
        }
        medicoRepository.findByCpf(cpf).ifPresent(m -> {
            if (idAtual == null || !m.getId().equals(idAtual)) {
                throw new RuntimeException("CPF já cadastrado para outro médico: " + cpf);
            }
        });
    }

    private String normalizarEspecialidade(String especialidade) {
        if (especialidade == null) {
            return null;
        }
        return switch (especialidade.trim().toUpperCase()) {
            case "CLINICO_GERAL", "CLÍNICO_GERAL", "CLINICO GERAL", "CLÍNICO GERAL" -> "Clínico Geral";
            case "CARDIOLOGISTA", "CARDIOLOGIA" -> "Cardiologia";
            case "DERMATOLOGISTA", "DERMATOLOGIA" -> "Dermatologia";
            case "PEDIATRA", "PEDIATRIA" -> "Pediatria";
            default -> especialidade;
        };
    }
}
