package com.example.gamesapi;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
// 1. Import mágico do HATEOAS adicionado:
import org.springframework.hateoas.RepresentationModel;

@Entity
// 2. Herança adicionada aqui:
public class Developer extends RepresentationModel<Developer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String country;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "developer")
    private java.util.List<Game> games;

    public Developer() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String n) { this.name = n; }

    public String getCountry() { return country; }
    public void setCountry(String c) { this.country = c; }

    public java.util.List<Game> getGames() { return games; }
    public void setGames(java.util.List<Game> games) { this.games = games; }
}