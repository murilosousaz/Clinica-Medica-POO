package br.uece.clinica.domain.repository;

import br.uece.clinica.domain.model.Conta;
import br.uece.clinica.domain.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface ContaRepository extends JpaRepository<Conta, UUID> {
    List<Conta> findByPaciente(Paciente paciente);

    List<Conta> findByPacienteAndSituacao(Paciente paciente, Conta.SituacaoConta situacao);

    List<Conta> findByPacienteAndSituacaoIn(Paciente paciente, Collection<Conta.SituacaoConta> situacoes);

    List<Conta> findBySituacaoOrderByDataVencimentoAsc(Conta.SituacaoConta situacao);

    List<Conta> findBySituacao(Conta.SituacaoConta situacao);

    long countBySituacao(Conta.SituacaoConta situacao);

    long countByPacienteAndSituacao(Paciente paciente, Conta.SituacaoConta situacao);
}
