package com.example.gamesapi;
import org.springframework.data.domain.Page;import org.springframework.data.domain.Pageable;import org.springframework.web.bind.annotation.*;import jakarta.validation.Valid;
@RestController
@RequestMapping("/players")
public class PlayerController{
private final PlayerRepository r;
public PlayerController(PlayerRepository r){this.r=r;}
@GetMapping public Page<Player> all(Pageable p){return r.findAll(p);}
@PostMapping public Player create(@Valid @RequestBody Player o){return r.save(o);}
@GetMapping("/{id}") public Player one(@PathVariable Long id){return r.findById(id).orElseThrow();}
@PutMapping("/{id}") public Player update(@Valid @RequestBody Player o,@PathVariable Long id){o.setId(id);return r.save(o);}
@DeleteMapping("/{id}") public void delete(@PathVariable Long id){r.deleteById(id);}
}