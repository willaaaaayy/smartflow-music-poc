package com.smartflow.dto;

import java.util.List;

/**
 * DTO для ответа Wave API (рекомендации)
 * 
 * Содержит персонализированный список рекомендованных треков
 * для пользователя на основе его предпочтений и истории.
 */
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

    public WaveResponseDTO() {
    }

    public WaveResponseDTO(List<TrackDTO> recommendations, Integer totalRecommendations, String source) {
        this.recommendations = recommendations;
        this.totalRecommendations = totalRecommendations;
        this.source = source;
    }

    // getters and setters

    public List<TrackDTO> getRecommendations() {
        return recommendations;
    }

    public void setRecommendations(List<TrackDTO> recommendations) {
        this.recommendations = recommendations;
    }

    public Integer getTotalRecommendations() {
        return totalRecommendations;
    }

    public void setTotalRecommendations(Integer totalRecommendations) {
        this.totalRecommendations = totalRecommendations;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}

