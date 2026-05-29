package br.uece.clinica.domain.repository;

import br.uece.clinica.domain.model.Enfermeiro;
import br.uece.clinica.domain.model.Paciente;
import br.uece.clinica.domain.model.Triagem;
import br.uece.clinica.domain.valueobject.PrioridadeSUS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface TriagemRepository extends JpaRepository<Triagem, UUID> {
    List<Triagem> findByPaciente(Paciente paciente);

    List<Triagem> findByEnfermeiro(Enfermeiro enfermeiro);

    @Query("SELECT t FROM Triagem t WHERE CAST(t.dataCriacao AS DATE) = :data ORDER BY t.prioridade ASC")
    List<Triagem> findTriagensDoiaDia(@Param("data") LocalDate data);

    @Query("SELECT t FROM Triagem t WHERE t.prioridade = :prioridade AND CAST(t.dataCriacao AS DATE) = CURRENT_DATE")
    List<Triagem> findByPrioridadeHoje(@Param("prioridade") PrioridadeSUS prioridade);

    @Query("SELECT COUNT(t) FROM Triagem t WHERE t.enfermeiro = :enfermeiro AND CAST(t.dataCriacao AS DATE) = CURRENT_DATE")
    int contarTriagensRealizadasHoje(@Param("enfermeiro") Enfermeiro enfermeiro);

    @Query("SELECT t FROM Triagem t WHERE t.prioridade IN ('VERMELHO', 'LARANJA') AND CAST(t.dataCriacao AS DATE) = CURRENT_DATE")
    List<Triagem> findTriagensEmergenciaHoje();
}