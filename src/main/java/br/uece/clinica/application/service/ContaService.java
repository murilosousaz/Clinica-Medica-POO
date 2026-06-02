package br.uece.clinica.application.service;

import br.uece.clinica.domain.model.Conta;
import br.uece.clinica.domain.model.Paciente;
import br.uece.clinica.domain.repository.ContaRepository;
import br.uece.clinica.domain.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ContaService {

    private final ContaRepository contaRepository;
    private final PacienteRepository pacienteRepository;

    public Conta salvar(Conta conta) {
        return contaRepository.save(conta);
    }

    @Transactional(readOnly = true)
    public Conta buscarPorId(UUID id) {

        return contaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<Conta> listarPendentes() {
        return contaRepository.findContasPendentes();
    }

    @Transactional(readOnly = true)
    public List<Conta> listarVencidas() {
        return contaRepository.findContasVencidas();
    }

    public Conta pagar(UUID id) {

        Conta conta = buscarPorId(id);

        conta.pagar();

        return contaRepository.save(conta);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularDebito(UUID pacienteId) {

        Paciente paciente =
                pacienteRepository.findById(pacienteId)
                        .orElseThrow();

        return contaRepository.calcularDevidoPaciente(paciente);
    }
}