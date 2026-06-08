package br.uece.clinica.application.service;

import br.uece.clinica.application.dto.ContaResponse;
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
    public ContaResponse buscarPorId(UUID id) {
        return toResponse(obterEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public List<ContaResponse> listarPendentes() {
        return contaRepository.findBySituacaoOrderByDataVencimentoAsc(Conta.SituacaoConta.PENDENTE)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ContaResponse> listarVencidas() {
        return contaRepository.findBySituacao(Conta.SituacaoConta.VENCIDA)
                .stream().map(this::toResponse).toList();
    }

    public ContaResponse pagar(UUID id) {
        Conta conta = obterEntidadePorId(id);
        conta.pagar();
        return toResponse(contaRepository.save(conta));
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularDebito(UUID pacienteId) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        return contaRepository.findByPacienteAndSituacaoIn(
                        paciente,
                        List.of(Conta.SituacaoConta.PENDENTE, Conta.SituacaoConta.VENCIDA)
                ).stream()
                .map(Conta::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Conta obterEntidadePorId(UUID id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
    }

    private ContaResponse toResponse(Conta conta) {
        return ContaResponse.builder()
                .id(conta.getId())
                .pacienteId(conta.getPaciente() != null ? conta.getPaciente().getId() : null)
                .pacienteNome(conta.getPaciente() != null ? conta.getPaciente().getNome() : null)
                .consultaId(conta.getConsulta() != null ? conta.getConsulta().getId() : null)
                .valor(conta.getValor())
                .descricao(conta.getDescricao())
                .dataVencimento(conta.getDataVencimento())
                .dataPagamento(conta.getDataPagamento())
                .situacao(conta.getSituacao() != null ? conta.getSituacao().name() : null)
                .build();
    }
}
