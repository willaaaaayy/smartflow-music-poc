package com.smartflow.model;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Модель музыкального трека
 * 
 * Хранит информацию о треке: название, исполнитель, длительность, жанры и метаданные.
 * Используется для поиска, рекомендаций и отображения в плейлистах.
 */
@Entity
@Table(name = "tracks", indexes = {
    @Index(name = "idx_track_title", columnList = "title"),
    @Index(name = "idx_track_artist", columnList = "artist"),
    @Index(name = "idx_track_created_at", columnList = "createdAt")
})
@EntityListeners(AuditingEntityListener.class)
public class Track {

    /**
     * Уникальный идентификатор трека
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Название трека
     */
    @Column(nullable = false, length = 200)
    private String title;

    /**
     * Исполнитель трека
     */
    @Column(nullable = false, length = 100)
    private String artist;

    /**
     * Альбом, к которому относится трек (опционально)
     */
    @Column(length = 200)
    private String album;

    /**
     * Длительность трека в секундах
     */
    @Column(nullable = false)
    private Integer duration;

    /**
     * URL для прослушивания трека
     */
    @Column(nullable = false, length = 500)
    private String audioUrl;

    /**
     * URL обложки трека
     */
    @Column(length = 500)
    private String coverUrl;

    /**
     * Жанры, к которым относится трек
     * Многие-ко-многим связь для гибкой категоризации
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "track_genres",
        joinColumns = @JoinColumn(name = "track_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    /**
     * Количество прослушиваний трека
     * Используется для ранжирования в рекомендациях
     */
    @Column(nullable = false)
    private Long playCount = 0L;

    /**
     * Дата добавления трека в систему
     */
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Дата последнего обновления информации о треке
     */
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Доступен ли трек для прослушивания
     */
    @Column(nullable = false)
    private Boolean isAvailable = true;

    public Track() {
        // Для JPA
    }

    public Track(Long id, String title, String artist, String album, Integer duration, String audioUrl,
                 String coverUrl, Set<Genre> genres, Long playCount, LocalDateTime createdAt,
                 LocalDateTime updatedAt, Boolean isAvailable) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.duration = duration;
        this.audioUrl = audioUrl;
        this.coverUrl = coverUrl;
        this.genres = genres != null ? genres : new HashSet<>();
        this.playCount = playCount != null ? playCount : 0L;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isAvailable = isAvailable != null ? isAvailable : true;
    }

    // getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public Set<Genre> getGenres() {
        return genres;
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public Long getPlayCount() {
        return playCount;
    }

    public void setPlayCount(Long playCount) {
        this.playCount = playCount;
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

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean available) {
        isAvailable = available;
    }
}

