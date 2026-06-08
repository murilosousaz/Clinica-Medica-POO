package br.uece.clinica.domain.repository;

import br.uece.clinica.domain.model.Enfermeiro;
import br.uece.clinica.domain.model.Paciente;
import br.uece.clinica.domain.model.Triagem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TriagemRepository extends JpaRepository<Triagem, UUID> {
    List<Triagem> findByPaciente(Paciente paciente);

    List<Triagem> findByEnfermeiro(Enfermeiro enfermeiro);

    List<Triagem> findByDataCriacaoBetweenOrderByPrioridadeAsc(LocalDateTime inicio, LocalDateTime fim);

    long countByEnfermeiroAndDataCriacaoBetween(Enfermeiro enfermeiro, LocalDateTime inicio, LocalDateTime fim);
}
