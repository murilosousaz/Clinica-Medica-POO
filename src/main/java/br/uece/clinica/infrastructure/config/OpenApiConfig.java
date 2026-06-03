package br.uece.clinica.infrastructure.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI clinicaMedicaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Sistema de Clínica Médica")
                        .description("Documentação dos endpoints REST do sistema de clínica médica desenvolvido para a disciplina de Programação Estruturada e Orientada a Objetos.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipe Clínica Médica POO")
                                .email("murilosz085@gmail.com"))
                        .license(new License()
                                .name("Uso acadêmico - UECE")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Ambiente local")
                ))
                .externalDocs(new ExternalDocumentation()
                        .description("Frontend do sistema")
                        .url("http://localhost:8080"));
    }
}
