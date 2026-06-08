package br.uece.clinica.application.mapper;

import br.uece.clinica.application.dto.CreateMedicoRequest;
import br.uece.clinica.application.dto.MedicoResponse;
import br.uece.clinica.domain.model.*;

import java.math.BigDecimal;

public final class MedicoMapper {

    private MedicoMapper() {}

    public static Medico toEntity(CreateMedicoRequest dto) {
        BigDecimal valor = valorConsulta(dto);
        Medico medico = switch (normalizarEspecialidade(dto.getEspecialidade())) {
            case "CLINICO_GERAL", "CLÍNICO_GERAL", "CLINICO GERAL", "CLÍNICO GERAL", "CLINICA_GERAL", "CLÍNICA_GERAL" -> new ClinicoGeral(dto.getNome(), dto.getCrm(), null, null, valor);
            case "CARDIOLOGISTA", "CARDIOLOGIA" -> new Cardiologista(dto.getNome(), dto.getCrm(), null, null, valor);
            case "DERMATOLOGISTA", "DERMATOLOGIA" -> new Dermatologista(dto.getNome(), dto.getCrm(), null, null, valor);
            case "PEDIATRA", "PEDIATRIA" -> new Pediatra(dto.getNome(), dto.getCrm(), null, null, valor);
            default -> throw new IllegalArgumentException("Especialidade inválida: " + dto.getEspecialidade());
        };
        medico.setCpf(dto.getCpf());
        atualizarPlanos(medico, dto);
        return medico;
    }

    public static void updateEntity(Medico medico, CreateMedicoRequest dto) {
        medico.setNome(dto.getNome());
        medico.setCrm(dto.getCrm());
        medico.setCpf(dto.getCpf());
        medico.setValorConsultaParticular(valorConsulta(dto));
        atualizarPlanos(medico, dto);
    }

    public static MedicoResponse toResponse(Medico medico) {
        return MedicoResponse.builder()
                .id(medico.getId())
                .nome(medico.getNome())
                .crm(medico.getCrm())
                .cpf(medico.getCpf())
                .especialidade(medico.getEspecialidade())
                .planosAtendidos(medico.getPlanosAtendidos())
                .valorConsultaParticular(medico.getValorConsultaParticularEfetivo())
                .mediaAvaliacoes(medico.getMediaAvaliacoes())
                .totalAvaliacoes(medico.getAvaliacoes().size())
                .maxPacientesPorDia(medico.getMaxPacientesPorDia())
                .ativo(medico.isAtivo())
                .build();
    }

    private static void atualizarPlanos(Medico medico, CreateMedicoRequest dto) {
        medico.getPlanosAtendidos().clear();
        if (dto.getPlanosAtendidos() == null) {
            return;
        }
        dto.getPlanosAtendidos().stream()
                .filter(plano -> plano != null && !plano.isBlank())
                .map(String::trim)
                .distinct()
                .forEach(medico::adicionarPlano);
    }

    private static BigDecimal valorConsulta(CreateMedicoRequest dto) {
        if (dto.getValorConsultaParticular() == null || dto.getValorConsultaParticular().compareTo(BigDecimal.ZERO) <= 0) {
            return new BigDecimal("100.00");
        }
        return dto.getValorConsultaParticular();
    }

    private static String normalizarEspecialidade(String especialidade) {
        if (especialidade == null || especialidade.isBlank()) {
            throw new IllegalArgumentException("Especialidade é obrigatória");
        }
        return especialidade.trim().toUpperCase();
    }
}
