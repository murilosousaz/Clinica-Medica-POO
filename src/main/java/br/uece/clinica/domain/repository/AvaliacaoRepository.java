package br.uece.clinica.domain.repository;

import br.uece.clinica.domain.model.Avaliacao;
import br.uece.clinica.domain.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, UUID> {
    @Query("SELECT a FROM Avaliacao a WHERE a.consulta.medico = :medico ORDER BY a.dataCriacao DESC")
    List<Avaliacao> findAvaliacoesMedico(@Param("medico") Medico medico);

    @Query("SELECT AVG(a.estrelas) FROM Avaliacao a WHERE a.consulta.medico = :medico")
    Double calcularMediaEstrelas(@Param("medico") Medico medico);

    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.consulta.medico = :medico")
    int contarAvaliacoes(@Param("medico") Medico medico);

    @Query("SELECT COUNT(a) FROM Avaliacao a WHERE a.consulta.medico = :medico AND a.estrelas >= 4")
    int contarAvaliacoesBoas(@Param("medico") Medico medico);

    @Query("SELECT a FROM Avaliacao a WHERE a.estrelas < 3 ORDER BY a.dataCriacao DESC")
    List<Avaliacao> findAvaliacoesPequenas();

    Optional<Avaliacao> findByConsultaId(UUID consultaId);
}