package com.example.gamesapi;
import org.springframework.data.domain.Page;import org.springframework.data.domain.Pageable;import org.springframework.web.bind.annotation.*;import jakarta.validation.Valid;
@RestController
@RequestMapping("/platforms")
public class PlatformController{
private final PlatformRepository r;
public PlatformController(PlatformRepository r){this.r=r;}
@GetMapping public Page<Platform> all(Pageable p){return r.findAll(p);}
@PostMapping public Platform create(@Valid @RequestBody Platform o){return r.save(o);}
@GetMapping("/{id}") public Platform one(@PathVariable Long id){return r.findById(id).orElseThrow();}
@PutMapping("/{id}") public Platform update(@Valid @RequestBody Platform o,@PathVariable Long id){o.setId(id);return r.save(o);}
@DeleteMapping("/{id}") public void delete(@PathVariable Long id){r.deleteById(id);}
}