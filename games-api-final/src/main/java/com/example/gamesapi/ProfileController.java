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
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Operação realizada com sucesso"),
        @ApiResponse(responseCode = "201", description = "Recurso criado com sucesso"),
        @ApiResponse(responseCode = "204", description = "Recurso deletado"),
        @ApiResponse(responseCode = "400", description = "Erro de validação"),
        @ApiResponse(responseCode = "404", description = "Não encontrado")
})
public class ProfileController {

    private final ProfileRepository r;
    public ProfileController(ProfileRepository r) { this.r = r; }

    @Operation(summary = "Listar perfis")
    @GetMapping
    public Page<Profile> all(Pageable p) {
        Page<Profile> page = r.findAll(p);
        page.forEach(prof -> prof.add(linkTo(methodOn(ProfileController.class).one(prof.getId())).withSelfRel()));
        return page;
    }

    @Operation(summary = "Criar perfil")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Profile create(@Valid @RequestBody Profile o) {
        if (o.getId() != null && o.getId() <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID inválido!");
        Profile saved = r.save(o);
        saved.add(linkTo(methodOn(ProfileController.class).one(saved.getId())).withSelfRel());
        saved.add(linkTo(methodOn(ProfileController.class).all(Pageable.unpaged())).withRel("todos_perfis"));
        return saved;
    }

    @Operation(summary = "Buscar por ID")
    @GetMapping("/{id}")
    public Profile one(@PathVariable @Positive Long id) {
        Profile prof = r.findById(id).orElseThrow();
        prof.add(linkTo(methodOn(ProfileController.class).one(id)).withSelfRel());
        prof.add(linkTo(methodOn(ProfileController.class).all(Pageable.unpaged())).withRel("todos_perfis"));
        return prof;
    }

    @Operation(summary = "Atualizar perfil")
    @PutMapping("/{id}")
    public Profile update(@Valid @RequestBody Profile o, @PathVariable @Positive Long id) {
        o.setId(id);
        Profile saved = r.save(o);
        saved.add(linkTo(methodOn(ProfileController.class).one(id)).withSelfRel());
        return saved;
    }

    @Operation(summary = "Deletar perfil")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) { r.deleteById(id); }

    @Operation(summary = "Buscar por nickname")
    @GetMapping("/search")
    public Page<Profile> searchByNickname(@RequestParam String nickname, Pageable p) {
        Page<Profile> page = r.findByNicknameContainingIgnoreCase(nickname, p);
        page.forEach(prof -> prof.add(linkTo(methodOn(ProfileController.class).one(prof.getId())).withSelfRel()));
        return page;
    }
}