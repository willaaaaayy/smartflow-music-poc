package com.smartflow.controller;

import com.smartflow.dto.WaveResponseDTO;
import com.smartflow.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для Wave API (рекомендации)
 * 
 * Предоставляет эндпоинты для получения персонализированных рекомендаций
 * для пользователей на основе их предпочтений и истории прослушиваний.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/wave")
@RequiredArgsConstructor
public class WaveController {

    private final RecommendationService recommendationService;

    /**
     * Получение персонализированных рекомендаций для пользователя
     * 
     * GET /api/v1/wave/recommendations?userId={userId}&limit={limit}
     * 
     * @param userId идентификатор пользователя (обязательный)
     * @param limit количество рекомендаций (по умолчанию 20, максимум 100)
     * @return список рекомендованных треков
     */
    @GetMapping("/recommendations")
    public ResponseEntity<WaveResponseDTO> getRecommendations(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "20") int limit) {
        
        log.info("Запрос рекомендаций для пользователя ID: {}, лимит: {}", userId, limit);

        // Ограничиваем максимальный лимит для защиты от перегрузки
        int safeLimit = Math.min(limit, 100);
        
        WaveResponseDTO recommendations = recommendationService.getRecommendations(userId, safeLimit);
        
        return ResponseEntity.ok(recommendations);
    }
}

