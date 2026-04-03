package com.example.gamesapi;
import org.springframework.data.domain.Page;import org.springframework.data.domain.Pageable;import org.springframework.web.bind.annotation.*;import jakarta.validation.Valid;
@RestController
@RequestMapping("/developers")
public class DeveloperController{
private final DeveloperRepository r;
public DeveloperController(DeveloperRepository r){this.r=r;}
@GetMapping public Page<Developer> all(Pageable p){return r.findAll(p);}
@PostMapping public Developer create(@Valid @RequestBody Developer o){return r.save(o);}
@GetMapping("/{id}") public Developer one(@PathVariable Long id){return r.findById(id).orElseThrow();}
@PutMapping("/{id}") public Developer update(@Valid @RequestBody Developer o,@PathVariable Long id){o.setId(id);return r.save(o);}
@DeleteMapping("/{id}") public void delete(@PathVariable Long id){r.deleteById(id);}
}