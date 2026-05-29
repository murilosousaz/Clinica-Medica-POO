package br.uece.clinica.domain.repository;

import br.uece.clinica.domain.model.Enfermeiro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnfermeiroRepository extends JpaRepository<Enfermeiro, UUID> {
    Optional<Enfermeiro> findByCoren(String coren);

    @Query("SELECT e FROM Enfermeiro e WHERE e.turno = :turno AND e.ativo = true")
    List<Enfermeiro> findByTurno(@Param("turno") Enfermeiro.Turno turno);

    @Query("SELECT e FROM Enfermeiro e WHERE e.especialidade = :especialidade AND e.ativo = true")
    List<Enfermeiro> findByEspecialidade(@Param("especialidade") String especialidade);

    @Query("SELECT e FROM Enfermeiro e WHERE e.ativo = true ORDER BY e.nome ASC")
    List<Enfermeiro> findAllAtivos();

    @Query("SELECT e FROM Enfermeiro e WHERE e.ativo = true ORDER BY e.totalTriagensRealizadas DESC")
    List<Enfermeiro> findMaisExperientes();

    @Query("SELECT e FROM Enfermeiro e WHERE e.ativo = true AND e.ultimoAcesso IS NOT NULL ORDER BY e.ultimoAcesso DESC")
    List<Enfermeiro> findEnfermeiroEmTurno();

    @Query("SELECT COUNT(t) FROM Triagem t WHERE t.enfermeiro = :enfermeiro AND CAST(t.dataCriacao AS DATE) = CURRENT_DATE")
    int contarTriagensHoje(@Param("enfermeiro") Enfermeiro enfermeiro);
}