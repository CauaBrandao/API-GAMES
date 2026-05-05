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
@RequestMapping("/platforms")
@Validated
@Tag(name = "platform-controller", description = "Gerenciamento de plataformas")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso"),
        @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso"),
        @ApiResponse(responseCode = "204", description = "Recurso deletado"),
        @ApiResponse(responseCode = "400", description = "Erro de validação"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
})
public class PlatformController {

    private final PlatformRepository r;
    public PlatformController(PlatformRepository r) { this.r = r; }

    @Operation(summary = "Listar plataformas")
    @GetMapping
    public Page<Platform> all(Pageable p) {
        Page<Platform> page = r.findAll(p);
        page.forEach(plat -> plat.add(linkTo(methodOn(PlatformController.class).one(plat.getId())).withSelfRel()));
        return page;
    }

    @Operation(summary = "Criar plataforma")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Platform create(@Valid @RequestBody Platform o) {
        if (o.getId() != null && o.getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido!");
        }
        Platform saved = r.save(o);
        saved.add(linkTo(methodOn(PlatformController.class).one(saved.getId())).withSelfRel());
        saved.add(linkTo(methodOn(PlatformController.class).all(Pageable.unpaged())).withRel("todas_plataformas"));
        return saved;
    }

    @Operation(summary = "Buscar por ID")
    @GetMapping("/{id}")
    public Platform one(@PathVariable @Positive Long id) {
        Platform plat = r.findById(id).orElseThrow();
        plat.add(linkTo(methodOn(PlatformController.class).one(id)).withSelfRel());
        plat.add(linkTo(methodOn(PlatformController.class).all(Pageable.unpaged())).withRel("todas_plataformas"));
        return plat;
    }

    @Operation(summary = "Atualizar")
    @PutMapping("/{id}")
    public Platform update(@Valid @RequestBody Platform o, @PathVariable @Positive Long id) {
        o.setId(id);
        Platform plat = r.save(o);
        plat.add(linkTo(methodOn(PlatformController.class).one(id)).withSelfRel());
        return plat;
    }

    @Operation(summary = "Deletar")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) { r.deleteById(id); }

    @Operation(summary = "Buscar por nome")
    @GetMapping("/search")
    public Page<Platform> searchByName(@RequestParam String name, Pageable p) {
        Page<Platform> page = r.findByNameContainingIgnoreCase(name, p);
        page.forEach(plat -> plat.add(linkTo(methodOn(PlatformController.class).one(plat.getId())).withSelfRel()));
        return page;
    }
}