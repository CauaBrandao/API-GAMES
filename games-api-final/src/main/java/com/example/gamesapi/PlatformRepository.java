package com.example.gamesapi;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlatformRepository extends JpaRepository<Platform, Long> {
    // Adicione esta linha abaixo para habilitar a busca do controller:
    Page<Platform> findByNameContainingIgnoreCase(String name, Pageable pageable);
}