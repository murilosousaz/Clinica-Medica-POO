package br.uece.clinica.application.mapper;

import br.uece.clinica.application.dto.CreateEnfermeiroRequest;
import br.uece.clinica.domain.model.Enfermeiro;

public final class EnfermeiroMapper {

    private EnfermeiroMapper() {}

    public static Enfermeiro toEntity(
            CreateEnfermeiroRequest dto) {

        return new Enfermeiro(
                dto.getNome(),
                dto.getCoren(),
                dto.getTurno(),
                dto.getAnosExperiencia()
        );
    }

    public static void updateEntity(
            Enfermeiro enfermeiro,
            CreateEnfermeiroRequest dto) {

        enfermeiro.setNome(dto.getNome());
        enfermeiro.setCoren(dto.getCoren());
        enfermeiro.setTurno(dto.getTurno());
        enfermeiro.setAnosExperiencia(
                dto.getAnosExperiencia()
        );
    }
}