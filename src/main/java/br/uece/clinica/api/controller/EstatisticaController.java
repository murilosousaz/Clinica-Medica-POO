package br.uece.clinica.api.controller;

import br.uece.clinica.application.service.EstatisticaService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Estatísticas", description = "Endpoints para estatísticas e dashboard do sistema.")
@RequestMapping("/api/estatisticas")
@RequiredArgsConstructor
public class EstatisticaController {

    private final EstatisticaService estatisticaService;

    @GetMapping
    public Map<String, Object> dashboard() {
        return estatisticaService.dashboard();
    }
}
