package com.example.gamesapi;
import jakarta.persistence.*;import jakarta.validation.constraints.*;
@Entity
public class Player{
@Id @GeneratedValue private Long id;
@NotBlank private String name;
@Email private String email;
public Player(){}
public Long getId(){return id;}public void setId(Long id){this.id=id;}
public String getName(){return name;}public void setName(String n){this.name=n;}
public String getEmail(){return email;}public void setEmail(String e){this.email=e;}
}