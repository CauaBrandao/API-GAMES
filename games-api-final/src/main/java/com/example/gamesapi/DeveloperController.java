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
@RequestMapping("/v1/developers")
@Validated
@Tag(name = "developer-controller", description = "Gerenciamento de desenvolvedoras")
public class DeveloperController {

    private final DeveloperRepository r;
    private final IdempotencyService idempotencyService;

    public DeveloperController(DeveloperRepository r, IdempotencyService idempotencyService) {
        this.r = r;
        this.idempotencyService = idempotencyService;
    }

    @Operation(summary = "Listar desenvolvedoras (paginado)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public Page<Developer> all(Pageable p) {
        Page<Developer> page = r.findAll(p);
        page.forEach(this::addLinks);
        return page;
    }

    @Operation(summary = "Criar desenvolvedora")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Desenvolvedora criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validacao nos dados enviados"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "409", description = "Requisicao duplicada (Idempotency-Key ja utilizada)"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Developer create(
            @Valid @RequestBody Developer o,
            @Parameter(description = "Chave de Idempotencia") @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey) {

        if (idempotencyKey != null && idempotencyService.isProcessed(idempotencyKey)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Requisicao duplicada bloqueada!");
        }

        if (o.getId() != null && o.getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID invalido!");
        }

        Developer saved = r.save(o);
        addLinks(saved);
        saved.add(linkTo(methodOn(DeveloperController.class).all(Pageable.unpaged())).withRel("todas_desenvolvedoras"));
        return saved;
    }

    @Operation(summary = "Buscar desenvolvedora por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Desenvolvedora encontrada"),
            @ApiResponse(responseCode = "400", description = "ID informado e invalido"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Desenvolvedora nao encontrada"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/{id}")
    public Developer one(@PathVariable @Positive Long id) {
        Developer dev = r.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Desenvolvedora com ID " + id + " nao encontrada."));
        addLinks(dev);
        dev.add(linkTo(methodOn(DeveloperController.class).all(Pageable.unpaged())).withRel("todas_desenvolvedoras"));
        return dev;
    }

    @Operation(summary = "Atualizar desenvolvedora")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Desenvolvedora atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validacao nos dados enviados"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Desenvolvedora nao encontrada"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PutMapping("/{id}")
    public Developer update(@Valid @RequestBody Developer o, @PathVariable @Positive Long id) {
        if (!r.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Desenvolvedora com ID " + id + " nao encontrada.");
        }
        o.setId(id);
        Developer dev = r.save(o);
        addLinks(dev);
        return dev;
    }

    @Operation(summary = "Deletar desenvolvedora")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Desenvolvedora deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID informado e invalido"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Desenvolvedora nao encontrada"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) {
        if (!r.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Desenvolvedora com ID " + id + " nao encontrada.");
        }
        r.deleteById(id);
    }

    @Operation(summary = "Buscar desenvolvedoras por nome")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados da busca retornados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parametro de busca ausente ou invalido"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Nenhuma desenvolvedora encontrada com o nome informado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/search")
    public Page<Developer> searchByName(@RequestParam String name, Pageable p) {
        Page<Developer> page = r.findByNameContainingIgnoreCase(name, p);
        if (page.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma desenvolvedora encontrada com o nome: " + name);
        }
        page.forEach(this::addLinks);
        return page;
    }

    private void addLinks(Developer dev) {
        if (!dev.hasLink("self")) {
            dev.add(linkTo(methodOn(DeveloperController.class).one(dev.getId())).withSelfRel());
            dev.add(linkTo(methodOn(DeveloperController.class).update(null, dev.getId())).withRel("update"));
            dev.add(linkTo(DeveloperController.class).slash(dev.getId()).withRel("delete"));
        }
    }
}