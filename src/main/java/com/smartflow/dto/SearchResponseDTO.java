package com.smartflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO для ответа на поисковый запрос
 * 
 * Содержит результаты поиска и метаданные для пагинации.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponseDTO {

    /**
     * Список найденных треков
     */
    private List<TrackDTO> tracks;

    /**
     * Общее количество найденных результатов
     */
    private Long totalElements;

    /**
     * Текущая страница (0-based)
     */
    private Integer currentPage;

    /**
     * Общее количество страниц
     */
    private Integer totalPages;

    /**
     * Размер страницы
     */
    private Integer pageSize;
}

