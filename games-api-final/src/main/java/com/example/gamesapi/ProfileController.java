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
@RequestMapping("/v1/profiles")
@Validated
@Tag(name = "profile-controller", description = "Gerenciamento de perfis de jogadores")
public class ProfileController {

    private final ProfileRepository r;
    public ProfileController(ProfileRepository r) { this.r = r; }

    @Operation(summary = "Listar perfis (paginado)")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Lista retornada com sucesso")})
    @GetMapping
    public Page<Profile> all(Pageable p) {
        Page<Profile> page = r.findAll(p);
        page.forEach(this::addLinks);
        return page;
    }

    @Operation(summary = "Criar perfil")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Perfil criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validacao")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Profile create(@Valid @RequestBody Profile o) {
        if (o.getId() != null && o.getId() <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID invalido!");
        Profile saved = r.save(o);
        addLinks(saved);
        saved.add(linkTo(methodOn(ProfileController.class).all(Pageable.unpaged())).withRel("todos_perfis"));
        return saved;
    }

    @Operation(summary = "Buscar perfil por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Perfil encontrado"),
            @ApiResponse(responseCode = "404", description = "Perfil nao encontrado")
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
            @ApiResponse(responseCode = "200", description = "Perfil atualizado"),
            @ApiResponse(responseCode = "400", description = "Erro de validacao"),
            @ApiResponse(responseCode = "404", description = "Perfil nao encontrado")
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
            @ApiResponse(responseCode = "204", description = "Perfil deletado"),
            @ApiResponse(responseCode = "404", description = "Perfil nao encontrado")
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
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Resultados da busca")})
    @GetMapping("/search")
    public Page<Profile> searchByNickname(@RequestParam String nickname, Pageable p) {
        Page<Profile> page = r.findByNicknameContainingIgnoreCase(nickname, p);
        page.forEach(this::addLinks);
        return page;
    }

    private void addLinks(Profile prof) {
        if (!prof.hasLink("self")) {
            prof.add(linkTo(methodOn(ProfileController.class).one(prof.getId())).withSelfRel());
        }
    }
}