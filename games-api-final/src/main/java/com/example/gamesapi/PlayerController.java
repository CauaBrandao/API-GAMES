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

    @GetMapping
    public Page<Player> all(Pageable p) {
        Page<Player> page = r.findAll(p);
        page.forEach(player -> player.add(linkTo(methodOn(PlayerController.class).one(player.getId())).withSelfRel()));
        return page;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Player create(
            @Valid @RequestBody Player o,
            @Parameter(description = "Chave de Idempotência (ex: 12345)") @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) {

        if (idempotencyKey != null && idempotencyService.isProcessed(idempotencyKey)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Requisição duplicada bloqueada! Esta chave de idempotência já foi processada.");
        }

        if (o.getId() != null && o.getId() <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        Player saved = r.save(o);
        saved.add(linkTo(methodOn(PlayerController.class).one(saved.getId())).withSelfRel());
        return saved;
    }

    @GetMapping("/{id}")
    public Player one(@PathVariable @Positive Long id) {
        
        Player player = r.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador não encontrado!"));
        player.add(linkTo(methodOn(PlayerController.class).one(id)).withSelfRel());
        return player;
    }

    @PutMapping("/{id}")
    public Player update(@Valid @RequestBody Player o, @PathVariable @Positive Long id) {
        o.setId(id);
        return r.save(o);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) {
        
        if (!r.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador não encontrado para exclusão!");
        }
        r.deleteById(id);
    }

    @Operation(summary = "Buscar jogadores por nome")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Resultados da busca")})
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
        }
    }
}