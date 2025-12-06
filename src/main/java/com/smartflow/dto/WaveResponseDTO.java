package com.smartflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO для ответа Wave API (рекомендации)
 * 
 * Содержит персонализированный список рекомендованных треков
 * для пользователя на основе его предпочтений и истории.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WaveResponseDTO {

    /**
     * Список рекомендованных треков
     */
    private List<TrackDTO> recommendations;

    /**
     * Общее количество рекомендаций
     */
    private Integer totalRecommendations;

    /**
     * Описание источника рекомендаций (для отладки)
     */
    private String source;
}

