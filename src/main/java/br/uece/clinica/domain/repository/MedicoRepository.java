package br.uece.clinica.domain.repository;

import br.uece.clinica.domain.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicoRepository extends JpaRepository<Medico, UUID> {
    Optional<Medico> findByCrm(String crm);

    List<Medico> findByEspecialidade(String especialidade);

    @Query("SELECT m FROM Medico m WHERE m.especialidade = :especialidade AND m.ativo = true")
    List<Medico> findByEspecialidadeAtivo(@Param("especialidade") String especialidade);

    @Query("SELECT m FROM Medico m WHERE :plano MEMBER OF m.planosAtendidos AND m.ativo = true")
    List<Medico> findByPlanosAtendidosContains(@Param("plano") String plano);

    @Query("SELECT m FROM Medico m WHERE m.ativo = true ORDER BY " +
            "(SELECT AVG(a.estrelas) FROM Avaliacao a WHERE a.consulta.medico = m) DESC")
    List<Medico> findMesBemAvaliados();

    @Query("SELECT COUNT(c) FROM Consulta c WHERE c.medico = :medico AND c.dataConsulta = CURRENT_DATE")
    int contarConsultasHoje(@Param("medico") Medico medico);
}