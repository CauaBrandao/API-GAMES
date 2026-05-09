package com.example.gamesapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/games")
@Validated
@Tag(name = "game-controller", description = "Gerenciamento do catálogo")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso"),
        @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso"),
        @ApiResponse(responseCode = "204", description = "Recurso deletado"),
        @ApiResponse(responseCode = "400", description = "Erro de validação"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
})
public class GameController {

    private final GameRepository r;
    public GameController(GameRepository r) { this.r = r; }

    @Operation(summary = "Listar jogos")
    @GetMapping
    public Page<Game> all(Pageable p) {
        Page<Game> page = r.findAll(p);
        page.forEach(game -> game.add(linkTo(methodOn(GameController.class).one(game.getId())).withSelfRel()));
        return page;
    }

    @Operation(summary = "Criar jogo")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Game create(@Valid @RequestBody Game o) {
        if (o.getId() != null && o.getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido!");
        }
        Game saved = r.save(o);
        saved.add(linkTo(methodOn(GameController.class).one(saved.getId())).withSelfRel());
        saved.add(linkTo(methodOn(GameController.class).all(Pageable.unpaged())).withRel("todos_jogos"));
        return saved;
    }

    @Operation(summary = "Buscar por ID")
    @GetMapping("/{id}")
    public Game one(@PathVariable @Positive Long id) {
        Game game = r.findById(id).orElseThrow();
        game.add(linkTo(methodOn(GameController.class).one(id)).withSelfRel());
        game.add(linkTo(methodOn(GameController.class).all(Pageable.unpaged())).withRel("todos_jogos"));
        return game;
    }

    @Operation(summary = "Atualizar")
    @PutMapping("/{id}")
    public Game update(@Valid @RequestBody Game o, @PathVariable @Positive Long id) {
        o.setId(id);
        Game game = r.save(o);
        game.add(linkTo(methodOn(GameController.class).one(id)).withSelfRel());
        return game;
    }

    @Operation(summary = "Deletar")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) { r.deleteById(id); }

    @Operation(summary = "Buscar por nome")
    @GetMapping("/search")
    public Page<Game> searchByName(@RequestParam String name, Pageable p) {
        Page<Game> page = r.findByNameContainingIgnoreCase(name, p);
        page.forEach(game -> game.add(linkTo(methodOn(GameController.class).one(game.getId())).withSelfRel()));
        return page;
    }
}