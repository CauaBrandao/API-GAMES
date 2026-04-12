package com.example.gamesapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/profiles")
@Validated
@Tag(name = "profile-controller", description = "Gerenciamento de perfis de jogadores/usuários")
public class ProfileController {

    private final ProfileRepository r;

    public ProfileController(ProfileRepository r) {
        this.r = r;
    }

    @Operation(summary = "Listar perfis", description = "Retorna uma lista paginada de todos os perfis cadastrados.")
    @GetMapping
    public Page<Profile> all(Pageable p) {
        return r.findAll(p);
    }

    @Operation(summary = "Criar perfil", description = "Adiciona um novo perfil ao banco de dados.")
    @PostMapping
    public Profile create(@Valid @RequestBody Profile o) {
        return r.save(o);
    }

    @Operation(summary = "Buscar perfil por ID", description = "Retorna os detalhes de um perfil específico.")
    @GetMapping("/{id}")
    public Profile one(@PathVariable @Positive(message = "O ID na URL deve ser maior que zero") Long id) {
        return r.findById(id).orElseThrow();
    }

    @Operation(summary = "Atualizar perfil", description = "Altera as informações de um perfil já existente.")
    @PutMapping("/{id}")
    public Profile update(@Valid @RequestBody Profile o, @PathVariable @Positive(message = "O ID na URL deve ser maior que zero") Long id) {
        o.setId(id);
        return r.save(o);
    }

    @Operation(summary = "Deletar perfil", description = "Remove permanentemente um perfil do banco de dados.")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive(message = "O ID na URL deve ser maior que zero") Long id) {
        r.deleteById(id);
    }
}