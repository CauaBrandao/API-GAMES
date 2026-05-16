package com.example.gamesapi;

import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class IdempotencyService {

    private final Set<String> processedKeys = ConcurrentHashMap.newKeySet();

    public boolean isProcessed(String key) {
        if (key == null || key.trim().isEmpty()) {
            return false;
        }

        return !processedKeys.add(key);
    }
}
