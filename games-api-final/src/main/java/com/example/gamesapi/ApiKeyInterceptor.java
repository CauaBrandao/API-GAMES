package com.example.gamesapi;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        // Libera a pagina do Swagger e H2 Console para testes
        if (path.contains("/swagger-ui") || path.contains("/v3/api-docs") || path.contains("/error") || path.contains("/h2-console")) {
            return true;
        }

        // Libera requisicoes OPTIONS (pre-flight CORS)
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        // Pega a chave que o usuario mandou no cabecalho
        String apiKey = request.getHeader("X-API-KEY");

        // Verifica se a chave eh igual a "senha123"
        if ("senha123".equals(apiKey)) {
            return true; // Chave correta, pode passar!
        } else {
            // Chave errada ou vazia: Erro 401 (Nao Autorizado)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"status\":401,\"error\":\"Acesso Negado\",\"message\":\"Chave da API invalida ou ausente! Envie o header X-API-KEY com o valor correto.\"}");
            return false; // Bloqueia a requisicao!
        }
    }
}
