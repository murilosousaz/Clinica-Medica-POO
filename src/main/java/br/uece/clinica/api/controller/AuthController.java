package br.uece.clinica.api.controller;

import br.uece.clinica.application.dto.CriarSenhaRequest;
import br.uece.clinica.application.dto.LoginRequest;
import br.uece.clinica.application.dto.LoginResponse;
import br.uece.clinica.application.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Login funcional por CPF e senha para pacientes, médicos e enfermeiros.")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Realiza login por CPF e senha")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/senha")
    @Operation(summary = "Define ou redefine a senha de um usuário cadastrado")
    public LoginResponse definirSenha(@Valid @RequestBody CriarSenhaRequest request) {
        return authService.definirSenha(request);
    }
}
