package com.smartflow.controller;

import com.smartflow.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Контроллер для управления конфигурацией рекомендаций через API
 * 
 * Позволяет n8n обновлять параметры алгоритма рекомендаций без перезагрузки приложения.
 * Используется для динамического управления бизнес-логикой через low-code платформу.
 */
@RestController
@RequestMapping("/api/v1/config")
public class ConfigController {

    private static final Logger log = LoggerFactory.getLogger(ConfigController.class);

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    /**
     * Получение текущей конфигурации рекомендаций
     * 
     * GET /api/v1/config/recommendations
     * 
     * @return текущая конфигурация
     */
    @GetMapping("/recommendations")
    public ResponseEntity<Map<String, Object>> getConfig() {
        log.info("Запрос текущей конфигурации рекомендаций");
        return ResponseEntity.ok(configService.getConfig());
    }

    /**
     * Обновление конфигурации рекомендаций через API
     * 
     * PUT /api/v1/config/recommendations
     * 
     * Этот эндпоинт вызывается из n8n workflow для обновления параметров в runtime.
     * Новая конфигурация применяется сразу без перезапуска приложения.
     * 
     * @param newConfig новая конфигурация
     * @return подтверждение обновления
     */
    @PutMapping("/recommendations")
    public ResponseEntity<Map<String, Object>> updateConfig(@RequestBody Map<String, Object> newConfig) {
        log.info("Обновление конфигурации рекомендаций через API");
        
        try {
            configService.updateConfig(newConfig);
            
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("status", "success");
            response.put("message", "Конфигурация успешно обновлена");
            response.put("config", configService.getConfig());
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            log.error("Ошибка валидации конфигурации: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "status", "error",
                            "message", "Ошибка валидации: " + e.getMessage()
                    ));
        } catch (Exception e) {
            log.error("Ошибка при обновлении конфигурации: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "status", "error",
                            "message", "Ошибка при обновлении конфигурации: " + e.getMessage()
                    ));
        }
    }

    /**
     * Перезагрузка конфигурации из файла
     * 
     * POST /api/v1/config/recommendations/reload
     * 
     * Этот эндпоинт вызывается из n8n workflow после обновления файла recommendation_config.json.
     * После обновления конфигурация перезагружается из файла.
     * 
     * @return подтверждение перезагрузки
     */
    @PostMapping("/recommendations/reload")
    public ResponseEntity<Map<String, Object>> reloadConfig() {
        log.info("Перезагрузка конфигурации рекомендаций из файла");
        
        try {
            // Перезагружаем конфигурацию из файла
            configService.loadConfig();
            
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("status", "success");
            response.put("message", "Конфигурация успешно перезагружена из файла");
            response.put("config", configService.getConfig());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Ошибка при перезагрузке конфигурации: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "status", "error",
                            "message", "Ошибка при перезагрузке конфигурации: " + e.getMessage()
                    ));
        }
    }
}
