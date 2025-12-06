package com.smartflow.controller;

import com.smartflow.dto.WaveResponseDTO;
import com.smartflow.model.Track;
import com.smartflow.model.User;
import com.smartflow.repository.TrackRepository;
import com.smartflow.repository.UserRepository;
import com.smartflow.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Контроллер для интеграции с Telegram ботом
 * 
 * Предоставляет эндпоинты для обработки команд Telegram бота:
 * - /mywave - получение персональных рекомендаций
 * - /like <trackId> - добавление трека в избранное
 * - /history - история прослушиваний
 * 
 * Эти эндпоинты вызываются из n8n workflow при получении команд от пользователя.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/telegram")
@RequiredArgsConstructor
public class TelegramController {

    private final RecommendationService recommendationService;
    private final UserRepository userRepository;
    private final TrackRepository trackRepository;

    /**
     * Webhook для получения команд от Telegram через n8n
     * 
     * POST /api/v1/telegram/webhook
     * 
     * @param request данные запроса от n8n с информацией о команде
     * @return ответ для отправки пользователю в Telegram
     */
    @PostMapping("/webhook")
    public ResponseEntity<Map<String, Object>> handleTelegramCommand(@RequestBody Map<String, Object> request) {
        log.info("Получена команда от Telegram: {}", request);

        String command = (String) request.getOrDefault("command", "");
        Long userId = getUserIdFromRequest(request);
        Map<String, Object> response = new HashMap<>();

        try {
            switch (command.toLowerCase()) {
                case "/mywave":
                    response = handleMyWaveCommand(userId);
                    break;
                case "/like":
                    Long trackId = getTrackIdFromRequest(request);
                    response = handleLikeCommand(userId, trackId);
                    break;
                case "/history":
                    response = handleHistoryCommand(userId);
                    break;
                default:
                    response.put("text", "Неизвестная команда. Доступные: /mywave, /like <trackId>, /history");
            }
        } catch (Exception e) {
            log.error("Ошибка обработки команды: {}", e.getMessage(), e);
            response.put("text", "Произошла ошибка при обработке команды");
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Обработка команды /mywave - получение рекомендаций
     */
    private Map<String, Object> handleMyWaveCommand(Long userId) {
        if (userId == null) {
            return createErrorResponse("Не указан userId");
        }

        WaveResponseDTO recommendations = recommendationService.getRecommendations(userId, 10);
        
        StringBuilder message = new StringBuilder();
        message.append("🎵 Ваша волна рекомендаций:\n\n");
        
        if (recommendations.getRecommendations().isEmpty()) {
            message.append("Пока нет рекомендаций. Добавьте любимые треки!");
        } else {
            int index = 1;
            for (var track : recommendations.getRecommendations()) {
                message.append(String.format("%d. %s - %s\n", 
                    index++, track.getArtist(), track.getTitle()));
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("text", message.toString());
        response.put("recommendations", recommendations);
        return response;
    }

    /**
     * Обработка команды /like - добавление трека в избранное
     */
    private Map<String, Object> handleLikeCommand(Long userId, Long trackId) {
        if (userId == null) {
            return createErrorResponse("Не указан userId");
        }
        if (trackId == null) {
            return createErrorResponse("Не указан trackId. Используйте: /like <trackId>");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return createErrorResponse("Пользователь не найден");
        }

        Optional<Track> trackOpt = trackRepository.findById(trackId);
        if (trackOpt.isEmpty()) {
            return createErrorResponse("Трек не найден");
        }

        User user = userOpt.get();
        Track track = trackOpt.get();

        if (user.getFavoriteTracks().contains(track)) {
            return createSuccessResponse("Трек уже в избранном: " + track.getTitle());
        }

        user.getFavoriteTracks().add(track);
        userRepository.save(user);

        Map<String, Object> response = new HashMap<>();
        response.put("text", "✅ Добавлено в избранное: " + track.getArtist() + " - " + track.getTitle());
        return response;
    }

    /**
     * Обработка команды /history - история прослушиваний
     */
    private Map<String, Object> handleHistoryCommand(Long userId) {
        if (userId == null) {
            return createErrorResponse("Не указан userId");
        }

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return createErrorResponse("Пользователь не найден");
        }

        User user = userOpt.get();
        var favoriteTracks = user.getFavoriteTracks();

        StringBuilder message = new StringBuilder();
        message.append("📜 Ваше избранное:\n\n");

        if (favoriteTracks.isEmpty()) {
            message.append("Пока нет избранных треков. Используйте /like <trackId>");
        } else {
            int index = 1;
            for (Track track : favoriteTracks) {
                message.append(String.format("%d. %s - %s\n", 
                    index++, track.getArtist(), track.getTitle()));
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("text", message.toString());
        return response;
    }

    /**
     * Извлечение userId из запроса
     */
    private Long getUserIdFromRequest(Map<String, Object> request) {
        Object userIdObj = request.get("userId");
        if (userIdObj instanceof Number) {
            return ((Number) userIdObj).longValue();
        }
        if (userIdObj instanceof String) {
            try {
                return Long.parseLong((String) userIdObj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Извлечение trackId из запроса
     */
    private Long getTrackIdFromRequest(Map<String, Object> request) {
        Object trackIdObj = request.get("trackId");
        if (trackIdObj instanceof Number) {
            return ((Number) trackIdObj).longValue();
        }
        if (trackIdObj instanceof String) {
            try {
                return Long.parseLong((String) trackIdObj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("text", "❌ " + message);
        return response;
    }

    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("text", message);
        return response;
    }
}
