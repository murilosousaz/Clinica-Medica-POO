package br.uece.clinica.application.mapper;

import br.uece.clinica.application.dto.TriagemRequest;
import br.uece.clinica.domain.model.Enfermeiro;
import br.uece.clinica.domain.model.Paciente;
import br.uece.clinica.domain.model.Triagem;

public final class TriagemMapper {

    private TriagemMapper() {}

    public static Triagem toEntity(
            TriagemRequest dto,
            Paciente paciente,
            Enfermeiro enfermeiro) {

        return new Triagem(
                paciente,
                enfermeiro,
                dto.getPrioridade(),
                dto.getQueixaPrincipal(),
                dto.getPressaoArterial(),
                dto.getFrequenciaCardiaca(),
                dto.getTemperatura(),
                dto.getFrequenciaRespiratoria(),
                dto.getPeso(),
                dto.getAltura()
        );
    }

    public static void updateEntity(
            Triagem triagem,
            TriagemRequest dto,
            Paciente paciente,
            Enfermeiro enfermeiro) {

        triagem.setPaciente(paciente);

        triagem.setEnfermeiro(
                enfermeiro
        );

        triagem.setPrioridade(
                dto.getPrioridade()
        );

        triagem.setQueixaPrincipal(
                dto.getQueixaPrincipal()
        );

        triagem.setPressaoArterial(
                dto.getPressaoArterial()
        );

        triagem.setFrequenciaCardiaca(
                dto.getFrequenciaCardiaca()
        );

        triagem.setTemperatura(
                dto.getTemperatura()
        );

        triagem.setFrequenciaRespiratoria(
                dto.getFrequenciaRespiratoria()
        );

        triagem.setPeso(
                dto.getPeso()
        );

        triagem.setAltura(
                dto.getAltura()
        );
    }
}