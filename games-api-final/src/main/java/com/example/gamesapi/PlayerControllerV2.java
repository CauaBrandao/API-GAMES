package com.example.gamesapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@Tag(name = "player-controller-v2", description = "Gerenciamento de jogadores (Versao 2 - Otimizada)")
public class PlayerControllerV2 {

    private final PlayerRepository r;

    public PlayerControllerV2(PlayerRepository r) {
        this.r = r;
    }

    @Operation(summary = "Listar jogadores (V2 - via URL)", description = "Nova versao da listagem via versionamento por URL.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso (V2)"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping("/v2/players")
    public Page<Player> allV2(Pageable p) {
        Page<Player> page = r.findAll(p);
        page.forEach(player -> {
            if (!player.hasLink("self")) {
                player.add(linkTo(methodOn(PlayerControllerV2.class).allV2(Pageable.unpaged())).withSelfRel());
            }
        });
        return page;
    }

    @Operation(summary = "Listar jogadores (V2 - via Header)", description = "Nova versao da listagem acessivel via header X-API-Version: 2 no endpoint /v1/players.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso (V2 via header)"),
            @ApiResponse(responseCode = "401", description = "Chave da API invalida ou ausente"),
            @ApiResponse(responseCode = "429", description = "Limite de requisicoes excedido"),
            @ApiResponse(responseCode = "500", description = "Erro interno do servidor")
    })
    @GetMapping(value = "/v1/players", headers = "X-API-Version=2")
    public Page<Player> allV2ByHeader(Pageable p) {
        Page<Player> page = r.findAll(p);
        page.forEach(player -> {
            if (!player.hasLink("self")) {
                player.add(linkTo(methodOn(PlayerControllerV2.class).allV2ByHeader(Pageable.unpaged())).withSelfRel());
            }
        });
        return page;
    }
}