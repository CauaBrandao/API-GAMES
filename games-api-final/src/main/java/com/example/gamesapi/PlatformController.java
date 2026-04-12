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
@RequestMapping("/platforms")
@Validated
@Tag(name = "platform-controller", description = "Gerenciamento de plataformas de jogos")
public class PlatformController {

    private final PlatformRepository r;

    public PlatformController(PlatformRepository r) {
        this.r = r;
    }

    @Operation(summary = "Listar plataformas", description = "Retorna uma lista paginada de todas as plataformas cadastradas.")
    @GetMapping
    public Page<Platform> all(Pageable p) {
        return r.findAll(p);
    }

    @Operation(summary = "Criar plataforma", description = "Adiciona uma nova plataforma ao banco de dados.")
    @PostMapping
    public Platform create(@Valid @RequestBody Platform o) {
        return r.save(o);
    }

    @Operation(summary = "Buscar plataforma por ID", description = "Retorna os detalhes de uma plataforma específica.")
    @GetMapping("/{id}")
    public Platform one(@PathVariable @Positive(message = "O ID na URL deve ser maior que zero") Long id) {
        return r.findById(id).orElseThrow();
    }

    @Operation(summary = "Atualizar plataforma", description = "Altera as informações de uma plataforma já existente.")
    @PutMapping("/{id}")
    public Platform update(@Valid @RequestBody Platform o, @PathVariable @Positive(message = "O ID na URL deve ser maior que zero") Long id) {
        o.setId(id);
        return r.save(o);
    }

    @Operation(summary = "Deletar plataforma", description = "Remove permanentemente uma plataforma do banco de dados.")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive(message = "O ID na URL deve ser maior que zero") Long id) {
        r.deleteById(id);
    }
}