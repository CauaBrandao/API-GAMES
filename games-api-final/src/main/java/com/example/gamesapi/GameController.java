package com.example.gamesapi;
import org.springframework.data.domain.Page;import org.springframework.data.domain.Pageable;import org.springframework.web.bind.annotation.*;import jakarta.validation.Valid;
@RestController
@RequestMapping("/games")
public class GameController{
private final GameRepository r;
public GameController(GameRepository r){this.r=r;}
@GetMapping public Page<Game> all(Pageable p){return r.findAll(p);}
@PostMapping public Game create(@Valid @RequestBody Game o){return r.save(o);}
@GetMapping("/{id}") public Game one(@PathVariable Long id){return r.findById(id).orElseThrow();}
@PutMapping("/{id}") public Game update(@Valid @RequestBody Game o,@PathVariable Long id){o.setId(id);return r.save(o);}
@DeleteMapping("/{id}") public void delete(@PathVariable Long id){r.deleteById(id);}
}