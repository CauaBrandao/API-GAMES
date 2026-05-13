package com.example.gamesapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v2/players")
@Tag(name = "player-controller-v2", description = "Gerenciamento de jogadores (Versão 2 - Otimizada)")
public class PlayerControllerV2 {

    private final PlayerRepository r;

    public PlayerControllerV2(PlayerRepository r) {
        this.r = r;
    }

    @Operation(summary = "Listar jogadores (V2)", description = "Nova versão da listagem, acessível via URL /v2/ ou header X-API-Version: 2.")
    @GetMapping(headers = "X-API-Version=2")
    public Page<Player> allV2ByHeader(Pageable p) {
        // Acessível via header X-API-Version: 2
        return r.findAll(p);
    }

    @Operation(summary = "Listar jogadores (V2 - via URL)", description = "Nova versão da listagem via versionamento por URL.")
    @GetMapping
    public Page<Player> allV2(Pageable p) {
        // Acessível via URL /v2/players
        return r.findAll(p);
    }
}