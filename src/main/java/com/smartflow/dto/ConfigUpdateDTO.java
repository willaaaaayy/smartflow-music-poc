package com.smartflow.dto;

import java.util.Map;

/**
 * DTO для обновления конфигурации рекомендаций
 * 
 * Используется n8n и другими внешними системами для динамического
 * изменения параметров алгоритма рекомендаций через REST API.
 */
public class ConfigUpdateDTO {

    /**
     * Коэффициенты усиления для жанров
     */
    private Map<String, Double> genreBoost;

    /**
     * Вес популярности треков
     */
    private Double popularityWeight;

    /**
     * Вес актуальности (новизны) треков
     */
    private Double recencyWeight;

    /**
     * Фактор случайности для разнообразия рекомендаций
     */
    private Double randomFactor;

    /**
     * Лимит рекомендаций по умолчанию
     */
    private Integer fallbackLimit;

    public ConfigUpdateDTO() {
    }

    public ConfigUpdateDTO(Map<String, Double> genreBoost, Double popularityWeight, Double recencyWeight,
                           Double randomFactor, Integer fallbackLimit) {
        this.genreBoost = genreBoost;
        this.popularityWeight = popularityWeight;
        this.recencyWeight = recencyWeight;
        this.randomFactor = randomFactor;
        this.fallbackLimit = fallbackLimit;
    }

    // getters and setters

    public Map<String, Double> getGenreBoost() {
        return genreBoost;
    }

    public void setGenreBoost(Map<String, Double> genreBoost) {
        this.genreBoost = genreBoost;
    }

    public Double getPopularityWeight() {
        return popularityWeight;
    }

    public void setPopularityWeight(Double popularityWeight) {
        this.popularityWeight = popularityWeight;
    }

    public Double getRecencyWeight() {
        return recencyWeight;
    }

    public void setRecencyWeight(Double recencyWeight) {
        this.recencyWeight = recencyWeight;
    }

    public Double getRandomFactor() {
        return randomFactor;
    }

    public void setRandomFactor(Double randomFactor) {
        this.randomFactor = randomFactor;
    }

    public Integer getFallbackLimit() {
        return fallbackLimit;
    }

    public void setFallbackLimit(Integer fallbackLimit) {
        this.fallbackLimit = fallbackLimit;
    }
}

