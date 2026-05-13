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
@RequestMapping("/v1/players")
@Validated
@Tag(name = "player-controller", description = "Gerenciamento de jogadores")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso"),
        @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso"),
        @ApiResponse(responseCode = "204", description = "Recurso deletado"),
        @ApiResponse(responseCode = "400", description = "Erro de validação"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
})
public class PlayerController {

    private final PlayerRepository r;
    // 1. Injetamos o nosso serviço de Idempotência aqui
    private final IdempotencyService idempotencyService;

    public PlayerController(PlayerRepository r, IdempotencyService idempotencyService) {
        this.r = r;
        this.idempotencyService = idempotencyService;
    }

    @Operation(summary = "Listar jogadores")
    @GetMapping
    public Page<Player> all(Pageable p) {
        Page<Player> page = r.findAll(p);
        page.forEach(player -> player.add(linkTo(methodOn(PlayerController.class).one(player.getId())).withSelfRel()));
        return page;
    }

    // 2. Trava de Idempotência adicionada no POST!
    @Operation(summary = "Criar jogador")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Player create(
            @Valid @RequestBody Player o,
            @Parameter(description = "Chave de Idempotência (ex: 12345)") @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey) {

        // Verifica se a chave já foi usada
        if (idempotencyKey != null && idempotencyService.isProcessed(idempotencyKey)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Requisição duplicada bloqueada! Esta chave de idempotência já foi processada.");
        }

        if (o.getId() != null && o.getId() <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido!");

        Player saved = r.save(o);
        saved.add(linkTo(methodOn(PlayerController.class).one(saved.getId())).withSelfRel());
        saved.add(linkTo(methodOn(PlayerController.class).all(Pageable.unpaged())).withRel("todos_jogadores"));
        return saved;
    }

    @Operation(summary = "Buscar por ID")
    @GetMapping("/{id}")
    public Player one(@PathVariable @Positive Long id) {
        Player player = r.findById(id).orElseThrow();
        player.add(linkTo(methodOn(PlayerController.class).one(id)).withSelfRel());
        player.add(linkTo(methodOn(PlayerController.class).all(Pageable.unpaged())).withRel("todos_jogadores"));
        return player;
    }

    @Operation(summary = "Atualizar jogador")
    @PutMapping("/{id}")
    public Player update(@Valid @RequestBody Player o, @PathVariable @Positive Long id) {
        o.setId(id);
        Player saved = r.save(o);
        saved.add(linkTo(methodOn(PlayerController.class).one(id)).withSelfRel());
        return saved;
    }

    @Operation(summary = "Deletar jogador")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) { r.deleteById(id); }

    @Operation(summary = "Buscar por nome")
    @GetMapping("/search")
    public Page<Player> searchByName(@RequestParam String name, Pageable p) {
        Page<Player> page = r.findByNameContainingIgnoreCase(name, p);
        page.forEach(player -> player.add(linkTo(methodOn(PlayerController.class).one(player.getId())).withSelfRel()));
        return page;
    }
}