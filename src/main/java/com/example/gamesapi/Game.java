package com.example.gamesapi;
import jakarta.persistence.*;import jakarta.validation.constraints.*;import java.util.List;
@Entity
public class Game{
@Id @GeneratedValue private Long id;
@NotBlank private String name;
@NotNull private Double price;
@Enumerated(EnumType.STRING) private Genre genre;
@ManyToMany private List<Platform> platforms;
@ManyToOne private Developer developer;
public Game(){}
public Long getId(){return id;}public void setId(Long id){this.id=id;}
public String getName(){return name;}public void setName(String n){this.name=n;}
public Double getPrice(){return price;}public void setPrice(Double p){this.price=p;}
public Genre getGenre(){return genre;}public void setGenre(Genre g){this.genre=g;}
public List<Platform> getPlatforms(){return platforms;}public void setPlatforms(List<Platform> p){this.platforms=p;}
public Developer getDeveloper(){return developer;}public void setDeveloper(Developer d){this.developer=d;}
}