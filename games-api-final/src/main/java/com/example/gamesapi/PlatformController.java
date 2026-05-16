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
@RequestMapping("/v1/platforms")
@Validated
@Tag(name = "platform-controller", description = "Gerenciamento de plataformas")
public class PlatformController {

    private final PlatformRepository r;
    private final IdempotencyService idempotencyService;

    public PlatformController(PlatformRepository r, IdempotencyService idempotencyService) {
        this.r = r;
        this.idempotencyService = idempotencyService;
    }

    @Operation(summary = "Listar plataformas (paginado)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
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
            @ApiResponse(responseCode = "400", description = "Erro de validacao nos dados enviados"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "409", description = "Requisicao duplicada (Idempotency-Key ja utilizada)"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Platform create(
            @Valid @RequestBody Platform o,
            @Parameter(description = "Chave de Idempotencia") @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey) {
        if (idempotencyKey != null && idempotencyService.isProcessed(idempotencyKey)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Requisicao duplicada bloqueada!");
        }
        if (o.getId() != null && o.getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID invalido!");
        }
        Platform saved = r.save(o);
        addLinks(saved);
        saved.add(linkTo(methodOn(PlatformController.class).all(Pageable.unpaged())).withRel("todas_plataformas"));
        return saved;
    }

    @Operation(summary = "Buscar plataforma por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plataforma encontrada"),
            @ApiResponse(responseCode = "400", description = "ID informado e invalido"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Plataforma nao encontrada"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/{id}")
    public Platform one(@PathVariable @Positive Long id) {
        Platform plat = r.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Plataforma com ID " + id + " nao encontrada."));
        addLinks(plat);
        plat.add(linkTo(methodOn(PlatformController.class).all(Pageable.unpaged())).withRel("todas_plataformas"));
        return plat;
    }

    @Operation(summary = "Atualizar plataforma")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Plataforma atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validacao nos dados enviados"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Plataforma nao encontrada"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PutMapping("/{id}")
    public Platform update(@Valid @RequestBody Platform o, @PathVariable @Positive Long id) {
        if (!r.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Plataforma com ID " + id + " nao encontrada.");
        }
        o.setId(id);
        Platform plat = r.save(o);
        addLinks(plat);
        return plat;
    }

    @Operation(summary = "Deletar plataforma")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Plataforma deletada com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID informado e invalido"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Plataforma nao encontrada"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) {
        if (!r.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Plataforma com ID " + id + " nao encontrada.");
        }
        r.deleteById(id);
    }

    @Operation(summary = "Buscar plataformas por nome")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados da busca retornados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parametro de busca ausente ou invalido"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Nenhuma plataforma encontrada com o nome informado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/search")
    public Page<Platform> searchByName(@RequestParam String name, Pageable p) {
        Page<Platform> page = r.findByNameContainingIgnoreCase(name, p);
        if (page.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhuma plataforma encontrada com o nome: " + name);
        }
        page.forEach(this::addLinks);
        return page;
    }

    private void addLinks(Platform plat) {
        if (!plat.hasLink("self")) {
            plat.add(linkTo(methodOn(PlatformController.class).one(plat.getId())).withSelfRel());
            plat.add(linkTo(methodOn(PlatformController.class).update(null, plat.getId())).withRel("update"));
            plat.add(linkTo(methodOn(PlatformController.class).delete(plat.getId())).withRel("delete"));
        }
    }
}