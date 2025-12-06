package com.smartflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO для передачи информации о треке через API
 * 
 * Используется для сериализации данных трека без лишней информации
 * и для оптимизации размера ответа API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackDTO {

    /**
     * Идентификатор трека
     */
    private Long id;

    /**
     * Название трека
     */
    private String title;

    /**
     * Исполнитель
     */
    private String artist;

    /**
     * Альбом
     */
    private String album;

    /**
     * Длительность в секундах
     */
    private Integer duration;

    /**
     * URL для прослушивания
     */
    private String audioUrl;

    /**
     * URL обложки
     */
    private String coverUrl;

    /**
     * Названия жанров трека
     */
    private Set<String> genres;

    /**
     * Количество прослушиваний
     */
    private Long playCount;
}

