package br.uece.clinica.api.controller;

import br.uece.clinica.application.service.CsvService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@Tag(name = "CSV", description = "Endpoints para exportação e importação de dados em arquivos CSV.")
@RequestMapping("/api/csv")
@RequiredArgsConstructor
public class CsvController {

    private final CsvService csvService;

    @PostMapping("/exportar")
    public Map<String, Object> exportar() {
        return csvService.exportarTudo();
    }

    @PostMapping("/importar")
    public Map<String, Object> importar() {
        return csvService.importarBasico();
    }
}
