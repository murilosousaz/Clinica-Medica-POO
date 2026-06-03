package br.uece.clinica.application.mapper;

import br.uece.clinica.application.dto.CreateMedicoRequest;
import br.uece.clinica.application.dto.MedicoResponse;
import br.uece.clinica.domain.model.*;

public final class MedicoMapper {

    private MedicoMapper() {}

    public static Medico toEntity(CreateMedicoRequest dto) {
        return switch (normalizarEspecialidade(dto.getEspecialidade())) {
            case "CLINICO_GERAL", "CLÍNICO_GERAL", "CLINICO GERAL", "CLÍNICO GERAL", "CLINICA_GERAL", "CLÍNICA_GERAL" -> new ClinicoGeral(dto.getNome(), dto.getCrm());
            case "CARDIOLOGISTA", "CARDIOLOGIA" -> new Cardiologista(dto.getNome(), dto.getCrm());
            case "DERMATOLOGISTA", "DERMATOLOGIA" -> new Dermatologista(dto.getNome(), dto.getCrm());
            case "PEDIATRA", "PEDIATRIA" -> new Pediatra(dto.getNome(), dto.getCrm());
            default -> throw new IllegalArgumentException("Especialidade inválida: " + dto.getEspecialidade());
        };
    }

    public static void updateEntity(Medico medico, CreateMedicoRequest dto) {
        medico.setNome(dto.getNome());
        medico.setCrm(dto.getCrm());
    }

    public static MedicoResponse toResponse(Medico medico) {
        return MedicoResponse.builder()
                .id(medico.getId())
                .nome(medico.getNome())
                .crm(medico.getCrm())
                .especialidade(medico.getEspecialidade())
                .ativo(medico.isAtivo())
                .build();
    }

    private static String normalizarEspecialidade(String especialidade) {
        if (especialidade == null || especialidade.isBlank()) {
            throw new IllegalArgumentException("Especialidade é obrigatória");
        }
        return especialidade.trim().toUpperCase();
    }
}
