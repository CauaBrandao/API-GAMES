package com.example.gamesapi;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Game Catalog API")
                        .version("v1.0.0")
                        .description("Bem-vindo à documentação oficial da Game Catalog API, desenvolvida como projeto final de Spring Boot. <br><br>" +
                                "Esta API RESTful robusta permite o gerenciamento completo de um ecossistema de jogos, incluindo o controle de plataformas, desenvolvedoras, jogos, jogadores e seus respectivos perfis. <br><br>" +
                                "Projetada com as melhores práticas de mercado, a aplicação implementa recursos avançados de arquitetura de software, tais como: navegação inteligente de dados via **HATEOAS**, políticas de segurança **CORS** para integração com aplicações front-end, **Versionamento** de endpoints (v1 e v2) para garantir a evolução segura do contrato, mecanismos de **Idempotência** para prevenir a duplicação de dados em requisições críticas e proteção global das rotas através de autenticação via **API Key**.")

                        .contact(new Contact()
                                .name("Cauã Brandão Moreira")
                                .email("cauamoreira.brandao@hotmail.com")
                                .url("https://github.com/CauaBrandao")))

                .addSecurityItem(new SecurityRequirement().addList("ApiKeyAuth"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("ApiKeyAuth", new SecurityScheme()
                                .name("X-API-KEY")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.HEADER)));
    }
}