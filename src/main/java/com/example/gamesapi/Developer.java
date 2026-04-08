package com.example.gamesapi;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Developer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "O ID da desenvolvedora deve ser um número real maior que zero.")
    private Long id;

    @NotBlank(message = "O nome da desenvolvedora não pode estar em branco.")
    private String name;

    @NotBlank(message = "O país não pode estar em branco.")
    private String country;

    public Developer() {
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String c) {
        this.country = c;
    }
}