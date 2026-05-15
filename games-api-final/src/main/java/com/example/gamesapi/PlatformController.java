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
@RequestMapping("/v1/platforms")
@Validated
@Tag(name = "platform-controller", description = "Gerenciamento de plataformas")
public class PlatformController {

    private final PlatformRepository r;
    public PlatformController(PlatformRepository r) { this.r = r; }

    @Operation(summary = "Listar plataformas (paginado)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")
    })
    @GetMapping
    public Page<Platform> all(Pageable p) {
        Page<Platform> page = r.findAll(p);
        page.forEach(this::addLinks);
        return page;
    }

    @Operation(summary = "Criar plataforma")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Plataforma criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos dados enviados")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Platform create(@Valid @RequestBody Platform o) {
        if (o.getId() != null && o.getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido!");
        }
        Platform saved = r.save(o);
        addLinks(saved);
        saved.add(linkTo(methodOn(PlatformController.class).all(Pageable.unpaged())).withRel("todas_plataformas"));
        return saved;
    }

    @Operation(summary = "Buscar plataforma por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plataforma encontrada"),
            @ApiResponse(responseCode = "404", description = "Plataforma não encontrada")
    })
    @GetMapping("/{id}")
    public Platform one(@PathVariable @Positive Long id) {
        Platform plat = r.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plataforma com ID " + id + " não encontrada."));
        addLinks(plat);
        plat.add(linkTo(methodOn(PlatformController.class).all(Pageable.unpaged())).withRel("todas_plataformas"));
        return plat;
    }

    @Operation(summary = "Atualizar plataforma")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plataforma atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação"),
            @ApiResponse(responseCode = "404", description = "Plataforma não encontrada")
    })
    @PutMapping("/{id}")
    public Platform update(@Valid @RequestBody Platform o, @PathVariable @Positive Long id) {
        if (!r.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Plataforma com ID " + id + " não encontrada.");
        }
        o.setId(id);
        Platform plat = r.save(o);
        addLinks(plat);
        return plat;
    }

    @Operation(summary = "Deletar plataforma")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Plataforma deletada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Plataforma não encontrada")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) {
        if (!r.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Plataforma com ID " + id + " não encontrada.");
        }
        r.deleteById(id);
    }

    @Operation(summary = "Buscar plataformas por nome")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados da busca")
    })
    @GetMapping("/search")
    public Page<Platform> searchByName(@RequestParam String name, Pageable p) {
        Page<Platform> page = r.findByNameContainingIgnoreCase(name, p);
        page.forEach(this::addLinks);
        return page;
    }

    private void addLinks(Platform plat) {
        if (!plat.hasLink("self")) {
            plat.add(linkTo(methodOn(PlatformController.class).one(plat.getId())).withSelfRel());
        }
    }
}