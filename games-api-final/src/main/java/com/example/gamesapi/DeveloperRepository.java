package com.example.gamesapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeveloperRepository extends JpaRepository<Developer, Long> {
    // Adicione esta linha abaixo para habilitar a busca do controller:
    Page<Developer> findByNameContainingIgnoreCase(String name, Pageable pageable);
}