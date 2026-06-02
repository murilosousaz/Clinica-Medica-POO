
package br.uece.clinica.application.service;

import br.uece.clinica.domain.model.Avaliacao;
import br.uece.clinica.domain.model.Medico;
import br.uece.clinica.domain.repository.AvaliacaoRepository;
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

    public Avaliacao salvar(Avaliacao avaliacao) {
        return avaliacaoRepository.save(avaliacao);
    }

    @Transactional(readOnly = true)
    public Avaliacao buscarPorId(UUID id) {

        return avaliacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avaliação não encontrada"));
    }

    @Transactional(readOnly = true)
    public Double mediaMedico(UUID medicoId) {

        Medico medico =
                medicoRepository.findById(medicoId)
                        .orElseThrow();

        return avaliacaoRepository.calcularMediaEstrelas(medico);
    }

    @Transactional(readOnly = true)
    public List<Avaliacao> listarRuins() {
        return avaliacaoRepository.findAvaliacoesPequenas();
    }

    @Transactional(readOnly = true)
    public List<Avaliacao> listarMedico(UUID medicoId) {

        Medico medico =
                medicoRepository.findById(medicoId)
                        .orElseThrow();

        return avaliacaoRepository.findAvaliacoesMedico(medico);
    }
}