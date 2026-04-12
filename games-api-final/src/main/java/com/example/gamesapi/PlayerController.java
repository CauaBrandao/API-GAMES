package com.example.gamesapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/players")
@Validated
@Tag(name = "player-controller", description = "Gerenciamento de jogadores")
public class PlayerController {

    private final PlayerRepository r;

    public PlayerController(PlayerRepository r) {
        this.r = r;
    }

    @Operation(summary = "Listar jogadores", description = "Retorna uma lista paginada de todos os jogadores cadastrados.")
    @GetMapping
    public Page<Player> all(Pageable p) {
        return r.findAll(p);
    }

    @Operation(summary = "Criar jogador", description = "Adiciona um novo jogador ao banco de dados.")
    @PostMapping
    public Player create(@Valid @RequestBody Player o) {
        return r.save(o);
    }

    @Operation(summary = "Buscar jogador por ID", description = "Retorna os detalhes de um jogador específico.")
    @GetMapping("/{id}")
    public Player one(@PathVariable @Positive(message = "O ID na URL deve ser maior que zero") Long id) {
        return r.findById(id).orElseThrow();
    }

    @Operation(summary = "Atualizar jogador", description = "Altera as informações de um jogador já existente.")
    @PutMapping("/{id}")
    public Player update(@Valid @RequestBody Player o, @PathVariable @Positive(message = "O ID na URL deve ser maior que zero") Long id) {
        o.setId(id);
        return r.save(o);
    }

    @Operation(summary = "Deletar jogador", description = "Remove permanentemente um jogador do banco de dados.")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive(message = "O ID na URL deve ser maior que zero") Long id) {
        r.deleteById(id);
    }
}