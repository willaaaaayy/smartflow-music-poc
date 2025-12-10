package com.smartflow.dto;

import java.util.List;

/**
 * DTO для ответа на поисковый запрос
 * 
 * Содержит результаты поиска и метаданные для пагинации.
 */
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

    public SearchResponseDTO() {
    }

    public SearchResponseDTO(List<TrackDTO> tracks, Long totalElements, Integer currentPage,
                             Integer totalPages, Integer pageSize) {
        this.tracks = tracks;
        this.totalElements = totalElements;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.pageSize = pageSize;
    }

    // getters and setters

    public List<TrackDTO> getTracks() {
        return tracks;
    }

    public void setTracks(List<TrackDTO> tracks) {
        this.tracks = tracks;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}

