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
@RequestMapping("/v1/profiles")
@Validated
@Tag(name = "profile-controller", description = "Gerenciamento de perfis")
public class ProfileController {

    private final ProfileRepository r;
    private final IdempotencyService idempotencyService;

    public ProfileController(ProfileRepository r, IdempotencyService idempotencyService) {
        this.r = r;
        this.idempotencyService = idempotencyService;
    }

    @Operation(summary = "Listar perfis (paginado)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping
    public Page<Profile> all(Pageable p) {
        Page<Profile> page = r.findAll(p);
        page.forEach(this::addLinks);
        return page;
    }

    @Operation(summary = "Criar perfil")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Perfil criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validacao nos dados enviados"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "409", description = "Requisicao duplicada (Idempotency-Key ja utilizada)"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Profile create(
            @Valid @RequestBody Profile o,
            @Parameter(description = "Chave de Idempotencia") @RequestHeader(value = "X-Idempotency-Key", required = false) String idempotencyKey) {
        if (idempotencyKey != null && idempotencyService.isProcessed(idempotencyKey)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Requisicao duplicada bloqueada!");
        }
        if (o.getId() != null && o.getId() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID invalido!");
        }
        Profile saved = r.save(o);
        addLinks(saved);
        saved.add(linkTo(methodOn(ProfileController.class).all(Pageable.unpaged())).withRel("todos_perfis"));
        return saved;
    }

    @Operation(summary = "Buscar perfil por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
            @ApiResponse(responseCode = "400", description = "ID informado e invalido"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Perfil nao encontrado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/{id}")
    public Profile one(@PathVariable @Positive Long id) {
        Profile prof = r.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil com ID " + id + " nao encontrado."));
        addLinks(prof);
        prof.add(linkTo(methodOn(ProfileController.class).all(Pageable.unpaged())).withRel("todos_perfis"));
        return prof;
    }

    @Operation(summary = "Atualizar perfil")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validacao nos dados enviados"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Perfil nao encontrado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @PutMapping("/{id}")
    public Profile update(@Valid @RequestBody Profile o, @PathVariable @Positive Long id) {
        if (!r.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil com ID " + id + " nao encontrado.");
        }
        o.setId(id);
        Profile saved = r.save(o);
        addLinks(saved);
        return saved;
    }

    @Operation(summary = "Deletar perfil")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Perfil deletado com sucesso"),
            @ApiResponse(responseCode = "400", description = "ID informado e invalido"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Perfil nao encontrado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) {
        if (!r.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil com ID " + id + " nao encontrado.");
        }
        r.deleteById(id);
    }

    @Operation(summary = "Buscar perfis por nickname")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Resultados da busca retornados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parametro de busca ausente ou invalido"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "404", description = "Nenhum perfil encontrado com o nickname informado"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/search")
    public Page<Profile> searchByNickname(@RequestParam String nickname, Pageable p) {
        Page<Profile> page = r.findByNicknameContainingIgnoreCase(nickname, p);
        if (page.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nenhum perfil encontrado com o nickname: " + nickname);
        }
        page.forEach(this::addLinks);
        return page;
    }

    private void addLinks(Profile prof) {
        if (!prof.hasLink("self")) {
            prof.add(linkTo(methodOn(ProfileController.class).one(prof.getId())).withSelfRel());
            prof.add(linkTo(methodOn(ProfileController.class).update(null, prof.getId())).withRel("update"));
            prof.add(linkTo(ProfileController.class).slash(prof.getId()).withRel("delete"));
        }
    }
}