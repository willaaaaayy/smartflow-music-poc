package com.smartflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * DTO для обновления конфигурации рекомендаций
 * 
 * Используется n8n и другими внешними системами для динамического
 * изменения параметров алгоритма рекомендаций через REST API.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}

