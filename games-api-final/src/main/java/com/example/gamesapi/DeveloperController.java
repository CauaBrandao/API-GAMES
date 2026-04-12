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
@RequestMapping("/developers")
@Validated
@Tag(name = "developer-controller", description = "Gerenciamento de desenvolvedoras de jogos")
public class DeveloperController {

    private final DeveloperRepository r;

    public DeveloperController(DeveloperRepository r) {
        this.r = r;
    }

    @Operation(summary = "Listar desenvolvedoras", description = "Retorna uma lista paginada de todas as desenvolvedoras cadastradas.")
    @GetMapping
    public Page<Developer> all(Pageable p) {
        return r.findAll(p);
    }

    @Operation(summary = "Criar desenvolvedora", description = "Adiciona uma nova desenvolvedora ao banco de dados.")
    @PostMapping
    public Developer create(@Valid @RequestBody Developer o) {
        return r.save(o);
    }

    @Operation(summary = "Buscar desenvolvedora por ID", description = "Retorna os detalhes de uma desenvolvedora específica.")
    @GetMapping("/{id}")
    public Developer one(@PathVariable @Positive(message = "O ID na URL deve ser maior que zero") Long id) {
        return r.findById(id).orElseThrow();
    }

    @Operation(summary = "Atualizar desenvolvedora", description = "Altera as informações de uma desenvolvedora já existente.")
    @PutMapping("/{id}")
    public Developer update(@Valid @RequestBody Developer o, @PathVariable @Positive(message = "O ID na URL deve ser maior que zero") Long id) {
        o.setId(id);
        return r.save(o);
    }

    @Operation(summary = "Deletar desenvolvedora", description = "Remove permanentemente uma desenvolvedora do banco de dados.")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable @Positive(message = "O ID na URL deve ser maior que zero") Long id) {
        r.deleteById(id);
    }
}