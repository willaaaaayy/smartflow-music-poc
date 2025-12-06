package com.smartflow.repository;

import com.smartflow.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий для работы с пользователями
 * 
 * Предоставляет методы для поиска и управления пользователями в базе данных.
 * Использует JPA для автоматической генерации SQL-запросов.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Поиск пользователя по email
     * 
     * @param email email пользователя
     * @return Optional с пользователем, если найден
     */
    Optional<User> findByEmail(String email);

    /**
     * Поиск пользователя по username
     * 
     * @param username имя пользователя
     * @return Optional с пользователем, если найден
     */
    Optional<User> findByUsername(String username);

    /**
     * Проверка существования пользователя с указанным email
     * 
     * @param email email для проверки
     * @return true, если пользователь существует
     */
    boolean existsByEmail(String email);

    /**
     * Проверка существования пользователя с указанным username
     * 
     * @param username username для проверки
     * @return true, если пользователь существует
     */
    boolean existsByUsername(String username);
}

