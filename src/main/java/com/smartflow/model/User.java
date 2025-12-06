package com.smartflow.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Модель пользователя музыкального сервиса
 * 
 * Хранит информацию о пользователе, его предпочтениях и истории прослушиваний.
 * Используется для персонализации рекомендаций и аналитики.
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email"),
    @Index(name = "idx_user_username", columnList = "username")
})
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Уникальный идентификатор пользователя
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальное имя пользователя для входа
     */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Email пользователя (уникальный)
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Имя пользователя для отображения
     */
    @Column(length = 100)
    private String displayName;

    /**
     * Множество жанров, которые предпочитает пользователь
     * Используется для улучшения качества рекомендаций
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_preferred_genres",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @Builder.Default
    private Set<Genre> preferredGenres = new HashSet<>();

    /**
     * Множество треков, которые пользователь добавил в избранное
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_favorite_tracks",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "track_id")
    )
    @Builder.Default
    private Set<Track> favoriteTracks = new HashSet<>();

    /**
     * Дата создания аккаунта
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата последнего обновления профиля
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Активен ли аккаунт пользователя
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
}

