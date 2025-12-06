package com.smartflow.model;

import jakarta.persistence.*;
import lombok.*;
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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}

