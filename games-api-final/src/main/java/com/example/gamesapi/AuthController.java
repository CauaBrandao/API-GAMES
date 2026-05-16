package com.example.gamesapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/auth")
@Tag(name = "auth-controller", description = "Gerenciamento de credenciais e chaves de API")
public class AuthController {

    @Operation(summary = "Gerar/Recuperar Chave de API", description = "Retorna a chave necessaria para acessar os endpoints protegidos.")
    @ApiResponse(responseCode = "200", description = "Chave retornada com sucesso")
    @GetMapping("/key")
    public Map<String, String> getApiKey() {
        return Map.of(
            "apiKey", "senha123",
            "header", "X-API-KEY",
            "status", "Ativa"
        );
    }
}
