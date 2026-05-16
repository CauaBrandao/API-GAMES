package com.example.gamesapi;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Min;
import org.springframework.hateoas.RepresentationModel;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Profile extends RepresentationModel<Profile> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nickname;

    @NotNull
    @Min(0)
    private Integer level;

    @JsonIgnore
    @OneToOne
    private Player player;

    public Profile() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNickname() { return nickname; }
    public void setNickname(String n) { this.nickname = n; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer l) { this.level = l; }
    public Player getPlayer() { return player; }
    public void setPlayer(Player p) { this.player = p; }
}