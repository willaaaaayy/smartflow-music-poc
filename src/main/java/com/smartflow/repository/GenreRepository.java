package com.smartflow.repository;

import com.smartflow.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с жанрами
 * 
 * Предоставляет методы для поиска и управления жанрами музыки.
 */
@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

    /**
     * Поиск жанра по названию (без учета регистра)
     * 
     * @param name название жанра
     * @return Optional с жанром, если найден
     */
    Optional<Genre> findByNameIgnoreCase(String name);

    /**
     * Проверка существования жанра с указанным названием
     * 
     * @param name название жанра
     * @return true, если жанр существует
     */
    boolean existsByNameIgnoreCase(String name);
}

