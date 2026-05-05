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
@RequestMapping("/developers")
@Validated
@Tag(name = "developer-controller", description = "Gerenciamento de desenvolvedoras")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso"),
        @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso"),
        @ApiResponse(responseCode = "204", description = "Recurso deletado"),
        @ApiResponse(responseCode = "400", description = "Erro de validação"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
})
public class DeveloperController {

    private final DeveloperRepository r;
    public DeveloperController(DeveloperRepository r) { this.r = r; }

    @Operation(summary = "Listar desenvolvedoras")
    @GetMapping
    public Page<Developer> all(Pageable p) {
        Page<Developer> page = r.findAll(p);
        page.forEach(dev -> dev.add(linkTo(methodOn(DeveloperController.class).one(dev.getId())).withSelfRel()));
        return page;
    }

    @Operation(summary = "Criar desenvolvedora")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Developer create(@Valid @RequestBody Developer o) {
        if (o.getId() != null && o.getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido!");
        }
        Developer saved = r.save(o);
        saved.add(linkTo(methodOn(DeveloperController.class).one(saved.getId())).withSelfRel());
        saved.add(linkTo(methodOn(DeveloperController.class).all(Pageable.unpaged())).withRel("todas_desenvolvedoras"));
        return saved;
    }

    @Operation(summary = "Buscar por ID")
    @GetMapping("/{id}")
    public Developer one(@PathVariable @Positive Long id) {
        Developer dev = r.findById(id).orElseThrow();
        dev.add(linkTo(methodOn(DeveloperController.class).one(id)).withSelfRel());
        dev.add(linkTo(methodOn(DeveloperController.class).all(Pageable.unpaged())).withRel("todas_desenvolvedoras"));
        return dev;
    }

    @Operation(summary = "Atualizar")
    @PutMapping("/{id}")
    public Developer update(@Valid @RequestBody Developer o, @PathVariable @Positive Long id) {
        o.setId(id);
        Developer dev = r.save(o);
        dev.add(linkTo(methodOn(DeveloperController.class).one(id)).withSelfRel());
        return dev;
    }

    @Operation(summary = "Deletar")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) { r.deleteById(id); }

    @Operation(summary = "Buscar por nome")
    @GetMapping("/search")
    public Page<Developer> searchByName(@RequestParam String name, Pageable p) {
        Page<Developer> page = r.findByNameContainingIgnoreCase(name, p);
        page.forEach(dev -> dev.add(linkTo(methodOn(DeveloperController.class).one(dev.getId())).withSelfRel()));
        return page;
    }
}