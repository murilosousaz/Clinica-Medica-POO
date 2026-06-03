package br.uece.clinica.application.service;

import br.uece.clinica.application.dto.CreateEnfermeiroRequest;
import br.uece.clinica.application.dto.EnfermeiroResponse;
import br.uece.clinica.application.mapper.EnfermeiroMapper;
import br.uece.clinica.domain.model.Enfermeiro;
import br.uece.clinica.domain.repository.EnfermeiroRepository;
import br.uece.clinica.domain.repository.TriagemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class EnfermeiroService {
    private final EnfermeiroRepository enfermeiroRepository;
    private final TriagemRepository triagemRepository;

    public EnfermeiroResponse cadastrar(CreateEnfermeiroRequest request) {
        validarCorenUnico(request.getCoren(), null);
        Enfermeiro enfermeiro = EnfermeiroMapper.toEntity(request);
        return EnfermeiroMapper.toResponse(enfermeiroRepository.save(enfermeiro));
    }

    public EnfermeiroResponse atualizar(UUID id, CreateEnfermeiroRequest request) {
        Enfermeiro enfermeiro = obterEntidadePorId(id);
        validarCorenUnico(request.getCoren(), id);
        EnfermeiroMapper.updateEntity(enfermeiro, request);
        return EnfermeiroMapper.toResponse(enfermeiroRepository.save(enfermeiro));
    }

    public void remover(UUID id) {
        Enfermeiro enfermeiro = obterEntidadePorId(id);
        enfermeiro.setAtivo(false);
        enfermeiroRepository.save(enfermeiro);
    }

    @Transactional(readOnly = true)
    public EnfermeiroResponse buscarPorId(UUID id) {
        return EnfermeiroMapper.toResponse(obterEntidadePorId(id));
    }

    @Transactional(readOnly = true)
    public List<EnfermeiroResponse> listarTodos() {
        return enfermeiroRepository.findAllAtivos().stream()
                .map(EnfermeiroMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Enfermeiro obterEntidadePorId(UUID id) {
        return enfermeiroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Enfermeiro não encontrado"));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obterEstatisticasEnfermeiro(UUID enfermeiroId) {
        Enfermeiro enfermeiro = obterEntidadePorId(enfermeiroId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("nome", enfermeiro.getNome());
        stats.put("coren", enfermeiro.getCoren());
        stats.put("especialidade", enfermeiro.getEspecialidade());
        stats.put("turno", enfermeiro.getTurno() != null ? enfermeiro.getTurno().getDescricao() : null);
        stats.put("totalTriagens", enfermeiro.getTotalTriagensRealizadas());
        stats.put("triagensHoje", triagemRepository.contarTriagensRealizadasHoje(enfermeiro));
        stats.put("ultimoAcesso", enfermeiro.getUltimoAcesso());
        stats.put("ativo", enfermeiro.isAtivo());
        return stats;
    }

    private void validarCorenUnico(String coren, UUID idAtual) {
        enfermeiroRepository.findByCoren(coren).ifPresent(e -> {
            if (idAtual == null || !e.getId().equals(idAtual)) {
                throw new RuntimeException("COREN já cadastrado: " + coren);
            }
        });
    }
}
