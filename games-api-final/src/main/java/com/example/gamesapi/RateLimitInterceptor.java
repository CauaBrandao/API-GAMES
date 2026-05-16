package com.example.gamesapi;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    // Limite: 50 requisicoes por janela de tempo
    private static final int MAX_REQUESTS = 50;
    // Janela de tempo: 60 segundos
    private static final long WINDOW_MS = 60_000;

    // Armazena contagem e timestamp por IP
    private final ConcurrentMap<String, long[]> requestCounts = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = request.getRemoteAddr();
        long now = System.currentTimeMillis();

        long[] record = requestCounts.compute(clientIp, (key, existing) -> {
            if (existing == null || (now - existing[1]) > WINDOW_MS) {
                return new long[]{1, now};
            }
            existing[0]++;
            return existing;
        });

        long count = record[0];
        long windowStart = record[1];
        long remaining = MAX_REQUESTS - count;
        long retryAfterSeconds = Math.max(1, (WINDOW_MS - (now - windowStart)) / 1000);

        // Headers informativos sobre o rate limit
        response.setHeader("X-RateLimit-Limit", String.valueOf(MAX_REQUESTS));
        response.setHeader("X-RateLimit-Remaining", String.valueOf(Math.max(0, remaining)));

        if (count > MAX_REQUESTS) {
            response.setStatus(429);
            response.setHeader("Retry-After", String.valueOf(retryAfterSeconds));
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(
                    "{\"timestamp\":\"" + LocalDateTime.now() + "\",\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Limite de requisicoes excedido. Tente novamente em " + retryAfterSeconds + " segundos.\"}");
            return false;
        }

        return true;
    }
}
