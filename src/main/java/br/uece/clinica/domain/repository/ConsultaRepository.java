package br.uece.clinica.domain.repository;

import br.uece.clinica.domain.model.Consulta;
import br.uece.clinica.domain.model.Medico;
import br.uece.clinica.domain.model.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ConsultaRepository extends JpaRepository<Consulta, UUID> {
    List<Consulta> findByPaciente(Paciente paciente);

    List<Consulta> findByMedico(Medico medico);

    @Query("SELECT c FROM Consulta c WHERE c.medico = :medico AND c.dataConsulta = :data ORDER BY c.horario ASC")
    List<Consulta> findConsultasDoMedicoNoDia(@Param("medico") Medico medico, @Param("data") LocalDate data);

    @Query("SELECT c FROM Consulta c WHERE c.status = 'REALIZADA' ORDER BY c.dataCriacao DESC")
    List<Consulta> findConsultasRealizadas();

    @Query("SELECT COUNT(c) FROM Consulta c WHERE c.medico = :medico")
    int contarConsultasDoMedico(@Param("medico") Medico medico);

    @Query("SELECT COUNT(c) FROM Consulta c WHERE c.paciente = :paciente")
    int contarConsultasDoPaciente(@Param("paciente") Paciente paciente);

    @Query("SELECT c FROM Consulta c WHERE c.dataConsulta = CURRENT_DATE AND c.status = 'AGENDADA'")
    List<Consulta> findConsultasHojeAgendadas();

    @Query("SELECT c FROM Consulta c WHERE c.dataConsulta BETWEEN :dataInicio AND :dataFim")
    List<Consulta> findConsultasEmPeriodo(@Param("dataInicio") LocalDate dataInicio, @Param("dataFim") LocalDate dataFim);
}