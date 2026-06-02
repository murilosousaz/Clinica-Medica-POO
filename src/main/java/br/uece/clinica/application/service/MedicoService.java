package br.uece.clinica.application.service;

import br.uece.clinica.domain.model.Medico;
import br.uece.clinica.domain.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MedicoService {

    private final MedicoRepository medicoRepository;

    public Medico salvar(Medico medico) {

        medicoRepository.findByCrm(medico.getCrm())
                .ifPresent(m -> {
                    throw new RuntimeException("CRM já cadastrado");
                });

        return medicoRepository.save(medico);
    }

    @Transactional(readOnly = true)
    public List<Medico> listarTodos() {
        return medicoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Medico buscarPorId(UUID id) {
        return medicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médico não encontrado"));
    }

    @Transactional(readOnly = true)
    public List<Medico> buscarPorEspecialidade(String especialidade) {
        return medicoRepository.findByEspecialidadeAtivo(especialidade);
    }

    @Transactional(readOnly = true)
    public List<Medico> rankingAvaliacoes() {
        return medicoRepository.findMesBemAvaliados();
    }

    public void desativar(UUID id) {

        Medico medico = buscarPorId(id);

        medico.setAtivo(false);

        medicoRepository.save(medico);
    }
}