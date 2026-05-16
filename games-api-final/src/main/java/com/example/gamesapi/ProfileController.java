package com.example.gamesapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/v1/profiles")
@Validated
@Tag(name = "profile-controller", description = "Gerenciamento de perfis")
public class ProfileController {

    private final ProfileRepository r;
    public ProfileController(ProfileRepository r) { this.r = r; }

    @GetMapping
    public Page<Profile> all(Pageable p) {
        Page<Profile> page = r.findAll(p);
        page.forEach(prof -> prof.add(linkTo(methodOn(ProfileController.class).one(prof.getId())).withSelfRel()));
        return page;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Profile create(@Valid @RequestBody Profile o) {
        if (o.getId() != null && o.getId() <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        Profile saved = r.save(o);
        saved.add(linkTo(methodOn(ProfileController.class).one(saved.getId())).withSelfRel());
        return saved;
    }

    @GetMapping("/{id}")
    public Profile one(@PathVariable @Positive Long id) {
        Profile prof = r.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado!"));
        prof.add(linkTo(methodOn(ProfileController.class).one(id)).withSelfRel());
        return prof;
    }

    @PutMapping("/{id}")
    public Profile update(@Valid @RequestBody Profile o, @PathVariable @Positive Long id) {
        o.setId(id);
        return r.save(o);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Positive Long id) {
        if (!r.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Perfil não encontrado para exclusão!");
        }
        r.deleteById(id);
    }
}