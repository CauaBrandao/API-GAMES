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
@Tag(name = "game-controller", description = "Gerenciamento do catálogo de jogos")
public class GameController {

    private final GameRepository r;
    public GameController(GameRepository r) { this.r = r; }

    @Operation(summary = "Listar jogos (paginado)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    @GetMapping
    public Page<Game> all(Pageable p) {
        Page<Game> page = r.findAll(p);
        page.forEach(this::addLinks);
        return page;
    }

    @Operation(summary = "Criar jogo")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Jogo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos dados enviados")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Game create(@Valid @RequestBody Game o) {
        if (o.getId() != null && o.getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido!");
        }
        Game saved = r.save(o);
        addLinks(saved);
        saved.add(linkTo(methodOn(GameController.class).all(Pageable.unpaged())).withRel("todos_jogos"));
        return saved;
    }

    @Operation(summary = "Buscar jogo por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jogo encontrado"),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
    })
    @GetMapping("/{id}")
    public Game one(@PathVariable @Positive Long id) {
        Game game = r.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo com ID " + id + " não encontrado."));
        addLinks(game);
        game.add(linkTo(methodOn(GameController.class).all(Pageable.unpaged())).withRel("todos_jogos"));
        return game;
    }

    @Operation(summary = "Atualizar jogo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jogo atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
    })
    @PutMapping("/{id}")
    public Game update(@Valid @RequestBody Game o, @PathVariable @Positive Long id) {
        if (!r.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo com ID " + id + " não encontrado.");
        }
        o.setId(id);
        Game game = r.save(o);
        addLinks(game);
        return game;
    }

    @Operation(summary = "Deletar jogo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Jogo deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Jogo não encontrado")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) {
        if (!r.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo com ID " + id + " não encontrado.");
        }
        r.deleteById(id);
    }

    @Operation(summary = "Buscar jogos por nome")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados da busca")
    })
    @GetMapping("/search")
    public Page<Game> searchByName(@RequestParam String name, Pageable p) {
        Page<Game> page = r.findByNameContainingIgnoreCase(name, p);
        page.forEach(this::addLinks);
        return page;
    }

    private void addLinks(Game game) {
        if (!game.hasLink("self")) {
            game.add(linkTo(methodOn(GameController.class).one(game.getId())).withSelfRel());
        }
    }
}