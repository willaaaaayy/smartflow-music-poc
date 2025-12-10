package com.smartflow.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Модель музыкального жанра
 * 
 * Представляет категорию музыки (рок, поп, джаз и т.д.).
 * Используется для классификации треков и персонализации рекомендаций.
 */
@Entity
@Table(name = "genres", indexes = {
    @Index(name = "idx_genre_name", columnList = "name")
})
@EntityListeners(AuditingEntityListener.class)
public class Genre {

    /**
     * Уникальный идентификатор жанра
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название жанра (уникальное)
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * Описание жанра
     */
    @Column(length = 500)
    private String description;

    /**
     * Дата создания записи о жанре
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата последнего обновления
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Genre() {
        // Для JPA
    }

    public Genre(Long id, String name, String description, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

