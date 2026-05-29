package br.uece.clinica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "br.uece.clinica.domain.repository")
@ComponentScan(basePackages = {
        "br.uece.clinica.api.controller",
        "br.uece.clinica.application.service",
        "br.uece.clinica.infrastructure"
})
public class ClinicaMedicaApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClinicaMedicaApplication.class, args);
    }
}