package com.example.gamesapi;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IdempotencyService {

    // Um "caderninho" na memória que anota as chaves que já foram usadas
    private final Set<String> processedKeys = ConcurrentHashMap.newKeySet();

    public boolean isProcessed(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false; // Se o cliente não mandar a chave, passa direto
        }

        return !processedKeys.add(key);
    }
}
