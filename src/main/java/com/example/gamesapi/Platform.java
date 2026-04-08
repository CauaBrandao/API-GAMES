package com.example.gamesapi;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Platform {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "O ID da plataforma deve ser um número real maior que zero.")
    private Long id;

    @NotBlank(message = "O nome da plataforma não pode estar em branco.")
    private String name;

    @NotBlank(message = "A empresa fabricante não pode estar em branco.")
    private String company;

    public Platform() {
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

    public String getCompany() {
        return company;
    }

    public void setCompany(String c) {
        this.company = c;
    }
}