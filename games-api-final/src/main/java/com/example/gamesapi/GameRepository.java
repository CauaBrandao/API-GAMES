package com.example.gamesapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
    // Adicione esta linha abaixo para habilitar a busca do controller:
    Page<Game> findByNameContainingIgnoreCase(String name, Pageable pageable);
}