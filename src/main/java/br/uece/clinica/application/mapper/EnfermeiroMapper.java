package br.uece.clinica.application.mapper;

import br.uece.clinica.application.dto.CreateEnfermeiroRequest;
import br.uece.clinica.application.dto.EnfermeiroResponse;
import br.uece.clinica.domain.model.Enfermeiro;

public final class EnfermeiroMapper {

    private EnfermeiroMapper() {}

    public static Enfermeiro toEntity(CreateEnfermeiroRequest dto) {
        Enfermeiro enfermeiro = new Enfermeiro(
                dto.getNome(),
                dto.getCoren(),
                null,
                null,
                null,
                parseTurno(dto.getTurno())
        );
        enfermeiro.setAnosExperiencia(dto.getAnosExperiencia());
        return enfermeiro;
    }

    public static void updateEntity(Enfermeiro enfermeiro, CreateEnfermeiroRequest dto) {
        enfermeiro.setNome(dto.getNome());
        enfermeiro.setCoren(dto.getCoren());
        enfermeiro.setTurno(parseTurno(dto.getTurno()));
        enfermeiro.setAnosExperiencia(dto.getAnosExperiencia());
    }

    public static EnfermeiroResponse toResponse(Enfermeiro enfermeiro) {
        return EnfermeiroResponse.builder()
                .id(enfermeiro.getId())
                .nome(enfermeiro.getNome())
                .coren(enfermeiro.getCoren())
                .turno(enfermeiro.getTurno() != null ? enfermeiro.getTurno().name() : null)
                .anosExperiencia(enfermeiro.getAnosExperiencia())
                .ativo(enfermeiro.isAtivo())
                .build();
    }

    private static Enfermeiro.Turno parseTurno(String turno) {
        if (turno == null || turno.isBlank()) {
            throw new IllegalArgumentException("Turno é obrigatório");
        }
        return Enfermeiro.Turno.valueOf(turno.trim().toUpperCase());
    }
}
