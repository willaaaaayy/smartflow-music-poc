package com.smartflow.controller;

import com.smartflow.dto.SearchResponseDTO;
import com.smartflow.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для поиска треков
 * 
 * Предоставляет эндпоинты для полнотекстового поиска по трекам
 * с поддержкой пагинации и фильтрации по жанрам.
 */
@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private static final Logger log = LoggerFactory.getLogger(SearchController.class);

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Поиск треков по запросу
     * 
     * GET /api/v1/search?query={query}&page={page}&size={size}
     * 
     * Выполняет полнотекстовый поиск по названию, исполнителю и альбому.
     * Результаты отсортированы по популярности (количество прослушиваний).
     * 
     * @param query поисковый запрос (обязательный)
     * @param page номер страницы (по умолчанию 0)
     * @param size размер страницы (по умолчанию 20, максимум 100)
     * @return результаты поиска с пагинацией
     */
    @GetMapping
    public ResponseEntity<SearchResponseDTO> searchTracks(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Поиск треков по запросу: '{}', страница: {}, размер: {}", query, page, size);

        // Валидация параметров
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Ограничиваем максимальный размер страницы
        int safeSize = Math.min(size, 100);
        int safePage = Math.max(0, page);

        SearchResponseDTO results = searchService.searchTracks(query.trim(), safePage, safeSize);
        
        return ResponseEntity.ok(results);
    }

    /**
     * Поиск треков по жанру
     * 
     * GET /api/v1/search/genre?genreId={genreId}&page={page}&size={size}
     * 
     * @param genreId идентификатор жанра (обязательный)
     * @param page номер страницы (по умолчанию 0)
     * @param size размер страницы (по умолчанию 20, максимум 100)
     * @return результаты поиска с пагинацией
     */
    @GetMapping("/genre")
    public ResponseEntity<SearchResponseDTO> searchTracksByGenre(
            @RequestParam Long genreId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.info("Поиск треков по жанру ID: {}, страница: {}, размер: {}", genreId, page, size);

        if (genreId == null) {
            return ResponseEntity.badRequest().build();
        }

        int safeSize = Math.min(size, 100);
        int safePage = Math.max(0, page);

        SearchResponseDTO results = searchService.searchTracksByGenre(genreId, safePage, safeSize);
        
        return ResponseEntity.ok(results);
    }
}

