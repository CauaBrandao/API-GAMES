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

        // Libera a página do Swagger para a gente conseguir testar visualmente
        if (path.contains("/swagger-ui") || path.contains("/v3/api-docs") || path.contains("/error")) {
            return true;
        }

        // Pega a chave que o usuário mandou no cabeçalho
        String apiKey = request.getHeader("X-API-KEY");

        // Verifica se a chave é igual a "senha123"
        if ("senha123".equals(apiKey)) {
            return true; // Chave correta, pode passar!
        } else {
            // Chave errada ou vazia: Erro 401 (Não Autorizado)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Acesso Negado: Chave da API invalida ou ausente!");
            return false; // Bloqueia a requisição!
        }
    }
}
