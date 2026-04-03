package com.example.gamesapi;
import jakarta.persistence.*;import jakarta.validation.constraints.*;
@Entity
public class Developer{
@Id @GeneratedValue private Long id;
@NotBlank private String name;
@NotBlank private String country;
public Developer(){}
public Long getId(){return id;}public void setId(Long id){this.id=id;}
public String getName(){return name;}public void setName(String n){this.name=n;}
public String getCountry(){return country;}public void setCountry(String c){this.country=c;}
}