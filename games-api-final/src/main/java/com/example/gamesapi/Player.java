package com.example.gamesapi;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
// 1. Import mágico do HATEOAS adicionado:
import org.springframework.hateoas.RepresentationModel;

@Entity
// 2. Herança adicionada aqui:
public class Player extends RepresentationModel<Player> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @Email
    private String email;

    public Player() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String n) { this.name = n; }

    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
}