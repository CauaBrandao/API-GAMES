package com.example.gamesapi;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final ApiKeyInterceptor apiKeyInterceptor;
    private final RateLimitInterceptor rateLimitInterceptor;

    public CorsConfig(ApiKeyInterceptor apiKeyInterceptor, RateLimitInterceptor rateLimitInterceptor) {
        this.apiKeyInterceptor = apiKeyInterceptor;
        this.rateLimitInterceptor = rateLimitInterceptor;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("X-RateLimit-Limit", "X-RateLimit-Remaining", "Retry-After");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(rateLimitInterceptor)
                .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**", "/error");
        registry.addInterceptor(apiKeyInterceptor)
                .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**", "/h2-console/**", "/error");
    }
}