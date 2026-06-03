package br.uece.clinica.api.controller;

import br.uece.clinica.application.service.CsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
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
