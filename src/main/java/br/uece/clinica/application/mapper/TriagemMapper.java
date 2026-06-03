package br.uece.clinica.application.mapper;

import br.uece.clinica.application.dto.TriagemRequest;
import br.uece.clinica.application.dto.TriagemResponse;
import br.uece.clinica.domain.model.Enfermeiro;
import br.uece.clinica.domain.model.Paciente;
import br.uece.clinica.domain.model.Triagem;

public final class TriagemMapper {

    private TriagemMapper() {}

    public static Triagem toEntity(TriagemRequest dto, Paciente paciente, Enfermeiro enfermeiro) {
        Triagem triagem = new Triagem(paciente, enfermeiro, dto.getPrioridade(), dto.getQueixaPrincipal());
        updateSinaisVitais(triagem, dto);
        return triagem;
    }

    public static void updateEntity(Triagem triagem, TriagemRequest dto, Paciente paciente, Enfermeiro enfermeiro) {
        triagem.setPaciente(paciente);
        triagem.setEnfermeiro(enfermeiro);
        triagem.setPrioridade(dto.getPrioridade());
        triagem.setQueixaPrincipal(dto.getQueixaPrincipal());
        updateSinaisVitais(triagem, dto);
    }

    public static TriagemResponse toResponse(Triagem triagem) {
        return TriagemResponse.builder()
                .id(triagem.getId())
                .pacienteId(triagem.getPaciente().getId())
                .pacienteNome(triagem.getPaciente().getNome())
                .enfermeiroId(triagem.getEnfermeiro().getId())
                .enfermeiroNome(triagem.getEnfermeiro().getNome())
                .prioridade(triagem.getPrioridade())
                .queixaPrincipal(triagem.getQueixaPrincipal())
                .dataHora(triagem.getDataCriacao())
                .temperatura(triagem.getTemperatura())
                .frequenciaCardiaca(triagem.getFrequenciaCardiaca())
                .frequenciaRespiratoria(triagem.getFrequenciaRespiratoria())
                .peso(triagem.getPeso())
                .altura(triagem.getAltura())
                .pressaoArterial(triagem.getPressaoArterial())
                .build();
    }

    private static void updateSinaisVitais(Triagem triagem, TriagemRequest dto) {
        triagem.setPressaoArterial(dto.getPressaoArterial());
        triagem.setFrequenciaCardiaca(dto.getFrequenciaCardiaca());
        triagem.setTemperatura(dto.getTemperatura());
        triagem.setFrequenciaRespiratoria(dto.getFrequenciaRespiratoria());
        triagem.setPeso(dto.getPeso());
        triagem.setAltura(dto.getAltura());
    }
}
