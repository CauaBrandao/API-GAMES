package com.example.gamesapi;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();

        if (path.contains("/swagger-ui") || path.contains("/v3/api-docs") || path.contains("/error") || path.contains("/h2-console")) {
            return true;
        }

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String apiKey = request.getHeader("X-API-KEY");

        if ("senha123".equals(apiKey)) {
            return true;
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    "{\"timestamp\":\"" + LocalDateTime.now() + "\",\"status\":401,\"error\":\"Acesso Negado\",\"message\":\"Chave da API invalida ou ausente! Envie o header X-API-KEY com o valor correto.\"}");
            return false;
        }
    }
}
