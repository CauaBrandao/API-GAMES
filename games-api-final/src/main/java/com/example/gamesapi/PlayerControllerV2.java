package com.example.gamesapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v2/players") // <-- AQUI ESTÁ A MÁGICA DA VERSÃO 2
@Tag(name = "player-controller-v2", description = "Gerenciamento de jogadores (Versão 2 - Otimizada)")
public class PlayerControllerV2 {

    private final PlayerRepository r;

    public PlayerControllerV2(PlayerRepository r) {
        this.r = r;
    }

    @Operation(summary = "Listar jogadores (V2)", description = "Nova versão da listagem, pronta para futuras quebras de contrato.")
    @GetMapping
    public Page<Player> allV2(Pageable p) {
        // Na vida real, a V2 teria regras diferentes, DTOs diferentes, etc.
        // Aqui, retornamos os mesmos dados, mas provamos que a rota /v2/ existe e funciona isolada da /v1/!
        return r.findAll(p);
    }

    // Deixamos apenas o GET para provar o conceito da V2 sem duplicar código desnecessário.
}