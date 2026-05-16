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
public class PlayerController {

    private final PlayerRepository r;
    private final IdempotencyService idempotencyService;

    public PlayerController(PlayerRepository r, IdempotencyService idempotencyService) {
        this.r = r;
        this.idempotencyService = idempotencyService;
    }

    @Operation(summary = "Listar jogadores (paginado)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public Page<Player> all(Pageable p) {
        Page<Player> page = r.findAll(p);
        page.forEach(this::addLinks);
        return page;
    }

    @Operation(summary = "Criar jogador")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Jogador criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validacao nos dados enviados"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "409", description = "Requisicao duplicada (Idempotency-Key ja utilizada)"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Player create(
            @Valid @RequestBody Player o,
            @Parameter(description = "Chave de Idempotencia") @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey) {
        if (idempotencyKey != null && idempotencyService.isProcessed(idempotencyKey)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Requisicao duplicada bloqueada!");
        }
        if (o.getId() != null && o.getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID invalido!");
        }
        Player saved = r.save(o);
        addLinks(saved);
        saved.add(linkTo(methodOn(PlayerController.class).all(Pageable.unpaged())).withRel("todos_jogadores"));
        return saved;
    }

    @Operation(summary = "Buscar jogador por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jogador encontrado"),
            @ApiResponse(responseCode = "400", description = "ID informado e invalido"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Jogador nao encontrado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/{id}")
    public Player one(@PathVariable @Positive Long id) {
        Player player = r.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador com ID " + id + " nao encontrado."));
        addLinks(player);
        player.add(linkTo(methodOn(PlayerController.class).all(Pageable.unpaged())).withRel("todos_jogadores"));
        return player;
    }

    @Operation(summary = "Atualizar jogador")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Jogador atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validacao nos dados enviados"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Jogador nao encontrado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PutMapping("/{id}")
    public Player update(@Valid @RequestBody Player o, @PathVariable @Positive Long id) {
        if (!r.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador com ID " + id + " nao encontrado.");
        }
        o.setId(id);
        Player saved = r.save(o);
        addLinks(saved);
        return saved;
    }

    @Operation(summary = "Deletar jogador")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Jogador deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID informado e invalido"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Jogador nao encontrado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) {
        if (!r.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador com ID " + id + " nao encontrado.");
        }
        r.deleteById(id);
    }

    @Operation(summary = "Buscar jogadores por nome")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados da busca retornados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parametro de busca ausente ou invalido"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Nenhum jogador encontrado com o nome informado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/search")
    public Page<Player> searchByName(@RequestParam String name, Pageable p) {
        Page<Player> page = r.findByNameContainingIgnoreCase(name, p);
        if (page.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum jogador encontrado com o nome: " + name);
        }
        page.forEach(this::addLinks);
        return page;
    }

    private void addLinks(Player player) {
        if (!player.hasLink("self")) {
            player.add(linkTo(methodOn(PlayerController.class).one(player.getId())).withSelfRel());
            player.add(linkTo(methodOn(PlayerController.class).update(null, player.getId())).withRel("update"));
            player.add(linkTo(PlayerController.class).slash(player.getId()).withRel("delete"));
        }
    }
}