package br.uece.clinica.domain.repository;

import br.uece.clinica.domain.model.ListaEspera;
import br.uece.clinica.domain.model.Medico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ListaEsperaRepository extends JpaRepository<ListaEspera, UUID> {

    List<ListaEspera> findByMedicoAndDataConsultaAndStatusOrderByDataCriacaoAsc(
            Medico medico,
            LocalDate dataConsulta,
            ListaEspera.StatusListaEspera status
    );

    List<ListaEspera> findByStatusOrderByDataConsultaAscDataCriacaoAsc(ListaEspera.StatusListaEspera status);
}
