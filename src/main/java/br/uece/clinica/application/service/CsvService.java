package br.uece.clinica.application.service;

import br.uece.clinica.domain.model.*;
import br.uece.clinica.domain.repository.*;
import br.uece.clinica.domain.valueobject.PlanoSaude;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CsvService {

    private static final Path DADOS_DIR = Path.of("dados");

    private final PacienteRepository pacienteRepository;
    private final MedicoRepository medicoRepository;
    private final ConsultaRepository consultaRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final ContaRepository contaRepository;
    private final ListaEsperaRepository listaEsperaRepository;

    public Map<String, Object> exportarTudo() {
        try {
            Files.createDirectories(DADOS_DIR);
            exportarPacientes();
            exportarMedicos();
            exportarConsultas();
            exportarAvaliacoes();
            exportarContas();
            exportarListaEspera();
            return Map.of("mensagem", "Arquivos CSV exportados com sucesso", "pasta", DADOS_DIR.toAbsolutePath().toString());
        } catch (IOException e) {
            throw new RuntimeException("Erro ao exportar CSV: " + e.getMessage(), e);
        }
    }

    @Transactional
    public Map<String, Object> importarBasico() {
        try {
            int pacientes = importarPacientes();
            int medicos = importarMedicos();
            return Map.of("mensagem", "Importação básica concluída", "pacientesImportados", pacientes, "medicosImportados", medicos);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao importar CSV: " + e.getMessage(), e);
        }
    }

    private void exportarPacientes() throws IOException {
        List<String> linhas = new ArrayList<>();
        linhas.add("id;nome;idade;cpf;telefone;email;plano;planoAtivo");
        for (Paciente p : pacienteRepository.findAll()) {
            PlanoSaude plano = p.getPlanoSaude();
            linhas.add(String.join(";",
                    valor(p.getId()), csv(p.getNome()), valor(p.getIdade()), csv(p.getCpf()), csv(p.getTelefone()), csv(p.getEmail()),
                    csv(plano != null ? plano.getNome() : "Não tenho"), valor(plano != null && plano.isAtivo())
            ));
        }
        Files.write(DADOS_DIR.resolve("pacientes.csv"), linhas, StandardCharsets.UTF_8);
    }

    private void exportarMedicos() throws IOException {
        List<String> linhas = new ArrayList<>();
        linhas.add("id;nome;crm;especialidade;planosAtendidos;valorConsultaParticular;ativo;maxPacientesPorDia");
        for (Medico m : medicoRepository.findAll()) {
            linhas.add(String.join(";",
                    valor(m.getId()), csv(m.getNome()), csv(m.getCrm()), csv(m.getEspecialidade()),
                    csv(String.join(",", m.getPlanosAtendidos())), valor(m.getValorConsultaParticular()), valor(m.isAtivo()), valor(m.getMaxPacientesPorDia())
            ));
        }
        Files.write(DADOS_DIR.resolve("medicos.csv"), linhas, StandardCharsets.UTF_8);
    }

    private void exportarConsultas() throws IOException {
        List<String> linhas = new ArrayList<>();
        linhas.add("id;pacienteCpf;pacienteNome;medicoCrm;medicoNome;dataHora;status;sintomas;diagnostico;tratamento;medicamentos;exames;valorPago;observacoes");
        for (Consulta c : consultaRepository.findAll()) {
            String sintomas = c.getDiagnostico() != null ? c.getDiagnostico().getSintomas() : "";
            String diagnostico = c.getDiagnostico() != null ? c.getDiagnostico().getDiagnosticoTexto() : "";
            String tratamento = c.getDiagnostico() != null ? c.getDiagnostico().getTratamentoSugerido() : "";
            linhas.add(String.join(";",
                    valor(c.getId()), csv(c.getPaciente().getCpf()), csv(c.getPaciente().getNome()), csv(c.getMedico().getCrm()), csv(c.getMedico().getNome()),
                    valor(c.getDataHora()), valor(c.getStatus()), csv(sintomas), csv(diagnostico), csv(tratamento), csv(c.getReceita()),
                    csv(String.join(",", c.getExamesSolicitados())), valor(c.getValorPago()), csv(c.getObservacoes())
            ));
        }
        Files.write(DADOS_DIR.resolve("consultas.csv"), linhas, StandardCharsets.UTF_8);
    }

    private void exportarAvaliacoes() throws IOException {
        List<String> linhas = new ArrayList<>();
        linhas.add("id;consultaId;medicoCrm;pacienteCpf;estrelas;texto");
        for (Avaliacao a : avaliacaoRepository.findAll()) {
            Consulta c = a.getConsulta();
            linhas.add(String.join(";", valor(a.getId()), valor(c.getId()), csv(c.getMedico().getCrm()), csv(c.getPaciente().getCpf()), valor(a.getEstrelas()), csv(a.getTexto())));
        }
        Files.write(DADOS_DIR.resolve("avaliacoes.csv"), linhas, StandardCharsets.UTF_8);
    }

    private void exportarContas() throws IOException {
        List<String> linhas = new ArrayList<>();
        linhas.add("id;pacienteCpf;consultaId;valor;descricao;dataVencimento;situacao");
        for (Conta c : contaRepository.findAll()) {
            linhas.add(String.join(";", valor(c.getId()), csv(c.getPaciente().getCpf()), valor(c.getConsulta() != null ? c.getConsulta().getId() : null), valor(c.getValor()), csv(c.getDescricao()), valor(c.getDataVencimento()), valor(c.getSituacao())));
        }
        Files.write(DADOS_DIR.resolve("contas.csv"), linhas, StandardCharsets.UTF_8);
    }

    private void exportarListaEspera() throws IOException {
        List<String> linhas = new ArrayList<>();
        linhas.add("id;pacienteCpf;pacienteNome;medicoCrm;medicoNome;dataConsulta;horarioDesejado;status;notificacao");
        for (ListaEspera item : listaEsperaRepository.findAll()) {
            linhas.add(String.join(";", valor(item.getId()), csv(item.getPaciente().getCpf()), csv(item.getPaciente().getNome()), csv(item.getMedico().getCrm()), csv(item.getMedico().getNome()), valor(item.getDataConsulta()), csv(item.getHorarioDesejado()), valor(item.getStatus()), csv(item.getNotificacao())));
        }
        Files.write(DADOS_DIR.resolve("lista_espera.csv"), linhas, StandardCharsets.UTF_8);
    }

    private int importarPacientes() throws IOException {
        Path arquivo = DADOS_DIR.resolve("pacientes.csv");
        if (!Files.exists(arquivo)) return 0;
        int importados = 0;
        List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
        for (int i = 1; i < linhas.size(); i++) {
            String[] c = linhas.get(i).split(";", -1);
            if (c.length < 8 || c[3].isBlank() || pacienteRepository.findByCpf(c[3]).isPresent()) continue;
            PlanoSaude plano = new PlanoSaude(limpar(c[6]), null, Boolean.parseBoolean(c[7]));
            Paciente paciente = new Paciente(limpar(c[1]), parseInt(c[2]), limpar(c[3]), limpar(c[4]), limpar(c[5]), plano);
            pacienteRepository.save(paciente);
            importados++;
        }
        return importados;
    }

    private int importarMedicos() throws IOException {
        Path arquivo = DADOS_DIR.resolve("medicos.csv");
        if (!Files.exists(arquivo)) return 0;
        int importados = 0;
        List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
        for (int i = 1; i < linhas.size(); i++) {
            String[] c = linhas.get(i).split(";", -1);
            if (c.length < 6 || c[2].isBlank() || medicoRepository.findByCrm(c[2]).isPresent()) continue;
            Medico medico = criarMedico(limpar(c[1]), limpar(c[2]), limpar(c[3]), parseBigDecimal(c[5]));
            if (c.length > 4 && !c[4].isBlank()) {
                for (String plano : c[4].split(",")) medico.adicionarPlano(plano.trim());
            }
            medicoRepository.save(medico);
            importados++;
        }
        return importados;
    }

    private Medico criarMedico(String nome, String crm, String especialidade, BigDecimal valor) {
        String normalizada = especialidade == null ? "" : especialidade.trim().toUpperCase();
        return switch (normalizada) {
            case "CARDIOLOGISTA", "CARDIOLOGIA" -> new Cardiologista(nome, crm, null, null, valor);
            case "PEDIATRA", "PEDIATRIA" -> new Pediatra(nome, crm, null, null, valor);
            case "DERMATOLOGISTA", "DERMATOLOGIA" -> new Dermatologista(nome, crm, null, null, valor);
            default -> new Cardiologista(nome, crm, null, null, valor);
        };
    }

    private String csv(Object valor) {
        return valor(valor).replace(";", ",").replace("\n", " ").replace("\r", " ");
    }

    private String valor(Object valor) {
        return Objects.toString(valor, "");
    }

    private String limpar(String valor) {
        return valor == null ? null : valor.trim();
    }

    private Integer parseInt(String valor) {
        try { return valor == null || valor.isBlank() ? null : Integer.parseInt(valor); } catch (NumberFormatException e) { return null; }
    }

    private BigDecimal parseBigDecimal(String valor) {
        try { return valor == null || valor.isBlank() ? BigDecimal.ZERO : new BigDecimal(valor); } catch (NumberFormatException e) { return BigDecimal.ZERO; }
    }
}
