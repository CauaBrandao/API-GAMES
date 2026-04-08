package com.example.gamesapi;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "O ID do jogador deve ser um número real maior que zero.")
    private Long id;

    @NotBlank(message = "O nome do jogador não pode estar em branco.")
    private String name;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "O formato do e-mail é inválido. Tente novamente.")
    private String email;

    public Player() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String n) {
        this.name = n;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String e) {
        this.email = e;
    }
}