package com.example.gamesapi;
import org.springframework.data.domain.Page;import org.springframework.data.domain.Pageable;import org.springframework.web.bind.annotation.*;import jakarta.validation.Valid;
@RestController
@RequestMapping("/profiles")
public class ProfileController{
private final ProfileRepository r;
public ProfileController(ProfileRepository r){this.r=r;}
@GetMapping public Page<Profile> all(Pageable p){return r.findAll(p);}
@PostMapping public Profile create(@Valid @RequestBody Profile o){return r.save(o);}
@GetMapping("/{id}") public Profile one(@PathVariable Long id){return r.findById(id).orElseThrow();}
@PutMapping("/{id}") public Profile update(@Valid @RequestBody Profile o,@PathVariable Long id){o.setId(id);return r.save(o);}
@DeleteMapping("/{id}") public void delete(@PathVariable Long id){r.deleteById(id);}
}