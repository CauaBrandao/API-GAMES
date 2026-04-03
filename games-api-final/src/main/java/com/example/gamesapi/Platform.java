package com.example.gamesapi;
import jakarta.persistence.*;import jakarta.validation.constraints.*;
@Entity
public class Platform{
@Id @GeneratedValue private Long id;
@NotBlank private String name;
@NotBlank private String company;
public Platform(){}
public Long getId(){return id;}public void setId(Long id){this.id=id;}
public String getName(){return name;}public void setName(String n){this.name=n;}
public String getCompany(){return company;}public void setCompany(String c){this.company=c;}
}