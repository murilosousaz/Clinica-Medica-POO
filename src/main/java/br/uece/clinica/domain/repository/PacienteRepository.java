package br.uece.clinica.domain.repository;

import br.uece.clinica.domain.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, UUID> {
    Optional<Paciente> findByCpf(String cpf);

    Optional<Paciente> findByEmail(String email);

    @Query("SELECT p FROM Paciente p WHERE p.nome LIKE %:nome% AND p.ativo = true")
    List<Paciente> searchPorNome(@Param("nome") String nome);

    @Query("SELECT p FROM Paciente p WHERE p.ativo = true ORDER BY p.nome ASC")
    List<Paciente> findAllAtivos();

    @Query("SELECT COUNT(c) FROM Consulta c WHERE c.paciente = :paciente")
    int contarConsultasRealizadas(@Param("paciente") Paciente paciente);

    @Query("SELECT p FROM Paciente p WHERE p.planoSaude.nome = :nomePlano AND p.ativo = true")
    List<Paciente> findByPlanosSaude(@Param("nomePlano") String nomePlano);
}