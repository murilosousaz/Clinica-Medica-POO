package br.uece.clinica.application.service;

import br.uece.clinica.domain.repository.ConsultaRepository;
import br.uece.clinica.domain.repository.ContaRepository;
import br.uece.clinica.domain.repository.MedicoRepository;
import br.uece.clinica.domain.repository.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EstatisticaService {

    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final ConsultaRepository consultaRepository;
    private final ContaRepository contaRepository;

    public Map<String, Object> dashboard() {

        Map<String, Object> dados = new HashMap<>();

        dados.put("pacientes", pacienteRepository.count());
        dados.put("medicos", medicoRepository.count());
        dados.put("consultas", consultaRepository.count());
        dados.put("contasPendentes", contaRepository.contarContasPendentes());

        return dados;
    }
}