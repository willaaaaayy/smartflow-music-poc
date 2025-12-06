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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Builder.Default
    private Set<Genre> genres = new HashSet<>();

    /**
     * Количество прослушиваний трека
     * Используется для ранжирования в рекомендациях
     */
    @Column(nullable = false)
    @Builder.Default
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
    @Builder.Default
    private Boolean isAvailable = true;
}

