package br.uece.clinica.domain.repository;

import br.uece.clinica.domain.model.Conta;
import br.uece.clinica.domain.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface ContaRepository extends JpaRepository<Conta, UUID> {
    List<Conta> findByPaciente(Paciente paciente);

    @Query("SELECT c FROM Conta c WHERE c.paciente = :paciente AND c.situacao = 'PENDENTE'")
    List<Conta> findContasPendentesDoPaciente(@Param("paciente") Paciente paciente);

    @Query("SELECT c FROM Conta c WHERE c.situacao = 'PENDENTE' ORDER BY c.dataVencimento ASC")
    List<Conta> findContasPendentes();

    @Query("SELECT c FROM Conta c WHERE c.situacao = 'VENCIDA'")
    List<Conta> findContasVencidas();

    @Query("SELECT SUM(c.valor) FROM Conta c WHERE c.paciente = :paciente AND c.situacao IN ('PENDENTE', 'VENCIDA')")
    BigDecimal calcularDevidoPaciente(@Param("paciente") Paciente paciente);

    @Query("SELECT COUNT(c) FROM Conta c WHERE c.situacao = 'PENDENTE'")
    int contarContasPendentes();

    @Query("SELECT COUNT(c) FROM Conta c WHERE c.paciente = :paciente AND c.situacao = 'PAGO'")
    int contarContasPagasDoPaciente(@Param("paciente") Paciente paciente);
}