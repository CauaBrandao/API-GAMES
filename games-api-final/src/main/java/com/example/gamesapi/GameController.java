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
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/games")
@Validated
@Tag(name = "game-controller", description = "Gerenciamento do catálogo de jogos")
public class GameController {

    private final GameRepository r;
    private final IdempotencyService idempotencyService;

    public GameController(GameRepository r, IdempotencyService idempotencyService) {
        this.r = r;
        this.idempotencyService = idempotencyService;
    }

    @Operation(summary = "Listar jogos (paginado)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
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
            @ApiResponse(responseCode = "400", description = "Erro de validacao nos dados enviados"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "409", description = "Requisicao duplicada (Idempotency-Key ja utilizada)"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Game create(
            @Valid @RequestBody Game o,
            @Parameter(description = "Chave de Idempotencia") @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey) {

        if (idempotencyKey != null && idempotencyService.isProcessed(idempotencyKey)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Requisicao duplicada bloqueada!");
        }

        if (o.getId() != null && o.getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID invalido!");
        }

        Game saved = r.save(o);
        addLinks(saved);
        saved.add(linkTo(methodOn(GameController.class).all(Pageable.unpaged())).withRel("todos_jogos"));
        return saved;
    }

    @Operation(summary = "Buscar jogo por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jogo encontrado"),
            @ApiResponse(responseCode = "400", description = "ID informado e invalido"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Jogo nao encontrado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/{id}")
    public Game one(@PathVariable @Positive Long id) {
        Game game = r.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo com ID " + id + " nao encontrado."));
        addLinks(game);
        game.add(linkTo(methodOn(GameController.class).all(Pageable.unpaged())).withRel("todos_jogos"));
        return game;
    }

    @Operation(summary = "Atualizar jogo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jogo atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validacao nos dados enviados"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Jogo nao encontrado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PutMapping("/{id}")
    public Game update(@Valid @RequestBody Game o, @PathVariable @Positive Long id) {
        if (!r.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo com ID " + id + " nao encontrado.");
        }
        o.setId(id);
        Game game = r.save(o);
        addLinks(game);
        return game;
    }

    @Operation(summary = "Deletar jogo")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Jogo deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID informado e invalido"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Jogo nao encontrado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) {
        if (!r.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogo com ID " + id + " nao encontrado.");
        }
        r.deleteById(id);
    }

    @Operation(summary = "Buscar jogos por nome")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados da busca retornados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parametro de busca ausente ou invalido"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Nenhum jogo encontrado com o nome informado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/search")
    public Page<Game> searchByName(@RequestParam String name, Pageable p) {
        Page<Game> page = r.findByNameContainingIgnoreCase(name, p);
        if (page.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum jogo encontrado com o nome: " + name);
        }
        page.forEach(this::addLinks);
        return page;
    }

    private void addLinks(Game game) {
        if (!game.hasLink("self")) {
            game.add(linkTo(methodOn(GameController.class).one(game.getId())).withSelfRel());
            game.add(linkTo(methodOn(GameController.class).update(null, game.getId())).withRel("update"));
            game.add(linkTo(methodOn(GameController.class).delete(game.getId())).withRel("delete"));
        }
    }
}