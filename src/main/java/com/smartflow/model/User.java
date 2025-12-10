package com.smartflow.model;

import jakarta.persistence.*;
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
    private Boolean isActive = true;

    public User() {
        // Для JPA
    }

    public User(Long id, String username, String email, String displayName, Set<Genre> preferredGenres,
                Set<Track> favoriteTracks, LocalDateTime createdAt, LocalDateTime updatedAt, Boolean isActive) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.displayName = displayName;
        this.preferredGenres = preferredGenres != null ? preferredGenres : new HashSet<>();
        this.favoriteTracks = favoriteTracks != null ? favoriteTracks : new HashSet<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isActive = isActive != null ? isActive : true;
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Set<Genre> getPreferredGenres() {
        return preferredGenres;
    }

    public void setPreferredGenres(Set<Genre> preferredGenres) {
        this.preferredGenres = preferredGenres;
    }

    public Set<Track> getFavoriteTracks() {
        return favoriteTracks;
    }

    public void setFavoriteTracks(Set<Track> favoriteTracks) {
        this.favoriteTracks = favoriteTracks;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}

