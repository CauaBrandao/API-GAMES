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
@RequestMapping("/games")
@Validated
@Tag(name = "game-controller", description = "Gerenciamento do catálogo de jogos")
public class GameController {

    private final GameRepository r;

    public GameController(GameRepository r) {
        this.r = r;
    }

    @Operation(summary = "Listar jogos", description = "Retorna uma lista paginada de todos os jogos cadastrados.")
    @GetMapping
    public Page<Game> all(Pageable p) {
        return r.findAll(p);
    }

    @Operation(summary = "Criar jogo", description = "Adiciona um novo jogo ao banco de dados.")
    @PostMapping
    public Game create(@Valid @RequestBody Game o) {
        return r.save(o);
    }

    @Operation(summary = "Buscar jogo por ID", description = "Retorna os detalhes de um jogo específico pelo seu ID.")
    @GetMapping("/{id}")
    public Game one(@PathVariable @Positive(message = "O ID na URL deve ser maior que zero") Long id) {
        return r.findById(id).orElseThrow();
    }

    @Operation(summary = "Atualizar jogo", description = "Altera as informações de um jogo já existente.")
    @PutMapping("/{id}")
    public Game update(@Valid @RequestBody Game o, @PathVariable @Positive(message = "O ID na URL deve ser maior que zero") Long id) {
        o.setId(id);
        return r.save(o);
    }

    @Operation(summary = "Deletar jogo", description = "Remove permanentemente um jogo do banco de dados.")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive(message = "O ID na URL deve ser maior que zero") Long id) {
        r.deleteById(id);
    }
}