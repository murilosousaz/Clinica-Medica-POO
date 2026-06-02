package br.uece.clinica.application.mapper;

import br.uece.clinica.application.dto.CreatePacienteRequest;
import br.uece.clinica.application.dto.PacienteResponse;
import br.uece.clinica.domain.model.Paciente;

public final class PacienteMapper {

    private PacienteMapper() {}

    public static Paciente toEntity(CreatePacienteRequest dto) {

        return new Paciente(
                dto.getNome(),
                dto.getIdade(),
                dto.getCpf(),
                dto.getTelefone(),
                dto.getEmail(),
                dto.getPlanoSaude()
        );
    }

    public static PacienteResponse toResponse(Paciente paciente) {

        return PacienteResponse.builder()
                .id(paciente.getId())
                .nome(paciente.getNome())
                .idade(paciente.getIdade())
                .cpf(paciente.getCpf())
                .telefone(paciente.getTelefone())
                .email(paciente.getEmail())
                .planoSaude(paciente.getPlanoSaude())
                .ativo(paciente.isAtivo())
                .build();
    }
}