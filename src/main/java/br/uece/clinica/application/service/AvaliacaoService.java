package br.uece.clinica.application.service;

import br.uece.clinica.application.dto.AvaliacaoRequest;
import br.uece.clinica.application.dto.AvaliacaoResponse;
import br.uece.clinica.domain.model.Avaliacao;
import br.uece.clinica.domain.model.Consulta;
import br.uece.clinica.domain.model.Medico;
import br.uece.clinica.domain.repository.AvaliacaoRepository;
import br.uece.clinica.domain.repository.ConsultaRepository;
import br.uece.clinica.domain.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final MedicoRepository medicoRepository;
    private final ConsultaRepository consultaRepository;

    public AvaliacaoResponse avaliar(AvaliacaoRequest request) {
        Consulta consulta = consultaRepository.findById(request.getConsultaId())
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        if (!consulta.podeSerAvaliada()) {
            throw new RuntimeException("A consulta precisa estar realizada e ainda não avaliada");
        }

        Avaliacao avaliacao = new Avaliacao(consulta, request.getTexto(), request.getEstrelas());
        Avaliacao salva = avaliacaoRepository.save(avaliacao);
        consulta.setAvaliacao(salva);
        return toResponse(salva);
    }

    public Avaliacao salvar(Avaliacao avaliacao) {
        return avaliacaoRepository.save(avaliacao);
    }

    @Transactional(readOnly = true)
    public AvaliacaoResponse buscarPorId(UUID id) {
        return toResponse(avaliacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada")));
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponse> listarTodas() {
        return avaliacaoRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public Double mediaMedico(UUID medicoId) {
        Medico medico = medicoRepository.findById(medicoId).orElseThrow(() -> new RuntimeException("Médico não encontrado"));
        Double media = avaliacaoRepository.calcularMediaEstrelas(medico);
        return media == null ? 0.0 : media;
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponse> listarRuins() {
        return avaliacaoRepository.findAvaliacoesPequenas().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoResponse> listarMedico(UUID medicoId) {
        Medico medico = medicoRepository.findById(medicoId).orElseThrow(() -> new RuntimeException("Médico não encontrado"));
        return avaliacaoRepository.findAvaliacoesMedico(medico).stream().map(this::toResponse).toList();
    }

    private AvaliacaoResponse toResponse(Avaliacao avaliacao) {
        Consulta consulta = avaliacao.getConsulta();
        return AvaliacaoResponse.builder()
                .id(avaliacao.getId())
                .consultaId(consulta.getId())
                .medicoId(consulta.getMedico().getId())
                .medicoNome(consulta.getMedico().getNome())
                .pacienteId(consulta.getPaciente().getId())
                .pacienteNome(consulta.getPaciente().getNome())
                .texto(avaliacao.getTexto())
                .estrelas(avaliacao.getEstrelas())
                .build();
    }
}
