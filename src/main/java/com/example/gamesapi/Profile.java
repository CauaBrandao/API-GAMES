package com.example.gamesapi;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Positive(message = "O ID do perfil deve ser um número real maior que zero.")
    private Long id;

    @NotBlank(message = "O apelido (nickname) não pode estar em branco.")
    private String nickname;

    @NotNull(message = "O nível do perfil é obrigatório.")
    @PositiveOrZero(message = "O nível do perfil não pode ser negativo.")
    private Integer level;

    @OneToOne
    private Player player;

    public Profile() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String n) {
        this.nickname = n;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer l) {
        this.level = l;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player p) {
        this.player = p;
    }
}