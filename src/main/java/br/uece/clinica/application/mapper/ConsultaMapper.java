package br.uece.clinica.application.mapper;

import br.uece.clinica.application.dto.ConsultaRequest;
import br.uece.clinica.application.dto.ConsultaResponse;
import br.uece.clinica.domain.model.Consulta;
import br.uece.clinica.domain.model.Medico;
import br.uece.clinica.domain.model.Paciente;

public final class ConsultaMapper {

    private ConsultaMapper() {}

    public static Consulta toEntity(ConsultaRequest dto, Paciente paciente, Medico medico) {
        return new Consulta(paciente, medico, dto.getDataHora(), dto.getObservacoes());
    }

    public static void updateEntity(Consulta consulta, ConsultaRequest dto, Paciente paciente, Medico medico) {
        consulta.setPaciente(paciente);
        consulta.setMedico(medico);
        consulta.atualizarDataHora(dto.getDataHora());
        consulta.setObservacoes(dto.getObservacoes());
    }

    public static ConsultaResponse toResponse(Consulta consulta) {
        return ConsultaResponse.builder()
                .id(consulta.getId())
                .pacienteId(consulta.getPaciente().getId())
                .pacienteNome(consulta.getPaciente().getNome())
                .medicoId(consulta.getMedico().getId())
                .medicoNome(consulta.getMedico().getNome())
                .medicoEspecialidade(consulta.getMedico().getEspecialidade())
                .dataHora(consulta.getDataHora())
                .status(consulta.getStatus().name())
                .observacoes(consulta.getObservacoes())
                .build();
    }
}
