package br.uece.clinica.application.mapper;

import br.uece.clinica.application.dto.CreateMedicoRequest;
import br.uece.clinica.domain.model.*;

public final class MedicoMapper {

    private MedicoMapper() {}

    public static Medico toEntity(CreateMedicoRequest dto) {

        return switch (
                dto.getEspecialidade().toUpperCase()
                ) {

            case "CARDIOLOGISTA" ->
                    new Cardiologista(
                            dto.getNome(),
                            dto.getCrm()
                    );

            case "DERMATOLOGISTA" ->
                    new Dermatologista(
                            dto.getNome(),
                            dto.getCrm()
                    );

            case "PEDIATRA" ->
                    new Pediatra(
                            dto.getNome(),
                            dto.getCrm()
                    );

            default ->
                    throw new IllegalArgumentException(
                            "Especialidade inválida"
                    );
        };
    }

    public static void updateEntity(
            Medico medico,
            CreateMedicoRequest dto) {

        medico.setNome(dto.getNome());
        medico.setCrm(dto.getCrm());
    }
}