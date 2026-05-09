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
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/players")
@Validated
@Tag(name = "player-controller")
public class PlayerController {

    private final PlayerRepository r;
    public PlayerController(PlayerRepository r) { this.r = r; }

    @GetMapping
    public Page<Player> all(Pageable p) {
        Page<Player> page = r.findAll(p);
        page.forEach(player -> player.add(linkTo(methodOn(PlayerController.class).one(player.getId())).withSelfRel()));
        return page;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Player create(@Valid @RequestBody Player o) {
        if (o.getId() != null && o.getId() <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Player saved = r.save(o);
        saved.add(linkTo(methodOn(PlayerController.class).one(saved.getId())).withSelfRel());
        return saved;
    }

    @GetMapping("/{id}")
    public Player one(@PathVariable @Positive Long id) {
        Player player = r.findById(id).orElseThrow();
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
    public void delete(@PathVariable @Positive Long id) { r.deleteById(id); }
}