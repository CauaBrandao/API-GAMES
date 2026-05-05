package com.example.gamesapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    // Adicione esta linha abaixo para habilitar a busca do controller:
    Page<Player> findByNameContainingIgnoreCase(String name, Pageable pageable);
}