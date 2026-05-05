package com.example.gamesapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Game Catalog API") // NOME DA API AQUI
                        .version("v1.0.0")
                        .description("API RESTful desenvolvida para o projeto final de Spring Boot. " +
                                "Esta API gerencia um catálogo completo de jogos, incluindo plataformas, " +
                                "desenvolvedoras e perfis de jogadores. Inclui recursos avançados como Rate Limiting.")
                        .contact(new Contact().name("Seu Nome Aqui").email("seu.email@aluno.com")));
    }
}