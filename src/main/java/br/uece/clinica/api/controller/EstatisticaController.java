package br.uece.clinica.api.controller;

import br.uece.clinica.application.service.EstatisticaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/estatisticas")
@RequiredArgsConstructor
public class EstatisticaController {

    private final EstatisticaService estatisticaService;

    @GetMapping
    public Map<String, Object> dashboard() {
        return estatisticaService.dashboard();
    }
}
