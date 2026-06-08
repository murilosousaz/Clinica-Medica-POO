package br.uece.clinica.application.service;

import br.uece.clinica.application.dto.CriarSenhaRequest;
import br.uece.clinica.application.dto.LoginRequest;
import br.uece.clinica.application.dto.LoginResponse;
import br.uece.clinica.domain.exception.LoginInvalidoException;
import br.uece.clinica.domain.model.Enfermeiro;
import br.uece.clinica.domain.model.Medico;
import br.uece.clinica.domain.model.Paciente;
import br.uece.clinica.domain.repository.EnfermeiroRepository;
import br.uece.clinica.domain.repository.MedicoRepository;
import br.uece.clinica.domain.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final EnfermeiroRepository enfermeiroRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        String cpf = normalizarCpf(request.getCpf());
        String perfil = normalizarPerfil(request.getPerfil());

        if ("PACIENTE".equals(perfil) || perfil == null) {
            var paciente = pacienteRepository.findByCpf(cpf);
            if (paciente.isPresent()) {
                return autenticarPaciente(paciente.get(), request.getSenha());
            }
            if ("PACIENTE".equals(perfil)) {
                throw new LoginInvalidoException("Paciente não encontrado para o CPF informado");
            }
        }

        if ("MEDICO".equals(perfil) || perfil == null) {
            var medico = medicoRepository.findByCpf(cpf);
            if (medico.isPresent()) {
                return autenticarMedico(medico.get(), request.getSenha());
            }
            if ("MEDICO".equals(perfil)) {
                throw new LoginInvalidoException("Médico não encontrado para o CPF informado");
            }
        }

        if ("ENFERMEIRO".equals(perfil) || perfil == null) {
            var enfermeiro = enfermeiroRepository.findByCpf(cpf);
            if (enfermeiro.isPresent()) {
                return autenticarEnfermeiro(enfermeiro.get(), request.getSenha());
            }
            if ("ENFERMEIRO".equals(perfil)) {
                throw new LoginInvalidoException("Enfermeiro não encontrado para o CPF informado");
            }
        }

        throw new LoginInvalidoException("CPF ou senha inválidos");
    }

    public LoginResponse definirSenha(CriarSenhaRequest request) {
        String cpf = normalizarCpf(request.getCpf());
        String perfil = normalizarPerfil(request.getPerfil());
        String senhaHash = criptografarSenha(request.getSenha());

        if ("PACIENTE".equals(perfil) || perfil == null) {
            var paciente = pacienteRepository.findByCpf(cpf);
            if (paciente.isPresent()) {
                Paciente p = paciente.get();
                p.setSenhaHash(senhaHash);
                pacienteRepository.save(p);
                return respostaPaciente(p, "Senha do paciente atualizada com sucesso");
            }
        }

        if ("MEDICO".equals(perfil) || perfil == null) {
            var medico = medicoRepository.findByCpf(cpf);
            if (medico.isPresent()) {
                Medico m = medico.get();
                m.setSenhaHash(senhaHash);
                medicoRepository.save(m);
                return respostaMedico(m, "Senha do médico atualizada com sucesso");
            }
        }

        if ("ENFERMEIRO".equals(perfil) || perfil == null) {
            var enfermeiro = enfermeiroRepository.findByCpf(cpf);
            if (enfermeiro.isPresent()) {
                Enfermeiro e = enfermeiro.get();
                e.setSenhaHash(senhaHash);
                enfermeiroRepository.save(e);
                return respostaEnfermeiro(e, "Senha do enfermeiro atualizada com sucesso");
            }
        }

        throw new LoginInvalidoException("Usuário não encontrado para criação de senha");
    }

    private LoginResponse autenticarPaciente(Paciente paciente, String senha) {
        if (!paciente.isAtivo()) {
            throw new LoginInvalidoException("Paciente inativo");
        }
        if (paciente.getSenhaHash() == null || paciente.getSenhaHash().isBlank()) {
            throw new LoginInvalidoException("Paciente ainda não possui senha cadastrada");
        }
        if (!passwordEncoder.matches(senha, paciente.getSenhaHash())) {
            throw new LoginInvalidoException("CPF ou senha inválidos");
        }
        return respostaPaciente(paciente, "Login realizado com sucesso");
    }

    private LoginResponse autenticarMedico(Medico medico, String senha) {
        if (!medico.isAtivo()) {
            throw new LoginInvalidoException("Médico inativo");
        }
        if (medico.getSenhaHash() == null || medico.getSenhaHash().isBlank()) {
            throw new LoginInvalidoException("Médico ainda não possui senha cadastrada");
        }
        if (!passwordEncoder.matches(senha, medico.getSenhaHash())) {
            throw new LoginInvalidoException("CPF ou senha inválidos");
        }
        return respostaMedico(medico, "Login realizado com sucesso");
    }

    private LoginResponse autenticarEnfermeiro(Enfermeiro enfermeiro, String senha) {
        if (!enfermeiro.isAtivo()) {
            throw new LoginInvalidoException("Enfermeiro inativo");
        }
        if (enfermeiro.getSenhaHash() == null || enfermeiro.getSenhaHash().isBlank()) {
            throw new LoginInvalidoException("Enfermeiro ainda não possui senha cadastrada");
        }
        if (!passwordEncoder.matches(senha, enfermeiro.getSenhaHash())) {
            throw new LoginInvalidoException("CPF ou senha inválidos");
        }
        enfermeiro.atualizarUltimoAcesso();
        enfermeiroRepository.save(enfermeiro);
        return respostaEnfermeiro(enfermeiro, "Login realizado com sucesso");
    }

    private LoginResponse respostaPaciente(Paciente paciente, String mensagem) {
        return LoginResponse.builder()
                .token(gerarToken("PACIENTE", paciente.getId().toString(), paciente.getCpf()))
                .perfil("PACIENTE")
                .usuarioId(paciente.getId())
                .nome(paciente.getNome())
                .cpf(paciente.getCpf())
                .mensagem(mensagem)
                .build();
    }

    private LoginResponse respostaMedico(Medico medico, String mensagem) {
        return LoginResponse.builder()
                .token(gerarToken("MEDICO", medico.getId().toString(), medico.getCpf()))
                .perfil("MEDICO")
                .usuarioId(medico.getId())
                .nome(medico.getNome())
                .cpf(medico.getCpf())
                .mensagem(mensagem)
                .build();
    }

    private LoginResponse respostaEnfermeiro(Enfermeiro enfermeiro, String mensagem) {
        return LoginResponse.builder()
                .token(gerarToken("ENFERMEIRO", enfermeiro.getId().toString(), enfermeiro.getCpf()))
                .perfil("ENFERMEIRO")
                .usuarioId(enfermeiro.getId())
                .nome(enfermeiro.getNome())
                .cpf(enfermeiro.getCpf())
                .mensagem(mensagem)
                .build();
    }

    private String criptografarSenha(String senha) {
        if (senha == null || senha.isBlank() || senha.length() < 6) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 6 caracteres");
        }
        return passwordEncoder.encode(senha);
    }

    private String normalizarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            throw new LoginInvalidoException("CPF é obrigatório");
        }
        return cpf.replaceAll("\\D", "");
    }

    private String normalizarPerfil(String perfil) {
        if (perfil == null || perfil.isBlank()) {
            return null;
        }
        String normalizado = perfil.trim().toUpperCase(Locale.ROOT);
        if (!normalizado.equals("PACIENTE") && !normalizado.equals("MEDICO") && !normalizado.equals("ENFERMEIRO")) {
            throw new IllegalArgumentException("Perfil inválido. Use PACIENTE, MEDICO ou ENFERMEIRO.");
        }
        return normalizado;
    }

    private String gerarToken(String perfil, String id, String cpf) {
        String payload = perfil + ":" + id + ":" + cpf + ":" + Instant.now().toEpochMilli();
        return Base64.getUrlEncoder().withoutPadding().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
    }
}
