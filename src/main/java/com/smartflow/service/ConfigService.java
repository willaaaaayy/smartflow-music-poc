package com.smartflow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Map;

/**
 * Сервис для загрузки и управления конфигурацией рекомендаций из JSON
 * 
 * Позволяет изменять параметры алгоритма рекомендаций без перекомпиляции кода.
 * Конфигурация загружается из файла recommendation_config.json при старте приложения.
 * 
 * Это даёт возможность:
 * - Динамически менять веса жанров
 * - Настраивать факторы релевантности
 * - Управлять поведением алгоритма через внешний JSON
 */
@Service
public class ConfigService {

    private Map<String, Object> config;

    /**
     * Конструктор загружает конфигурацию при создании сервиса
     */
    public ConfigService() {
        loadConfig();
    }

    /**
     * Загрузка конфигурации из JSON файла
     * 
     * Читает файл recommendation_config.json из classpath и парсит его в Map.
     * В случае ошибки выбрасывает RuntimeException для предотвращения запуска
     * приложения с невалидной конфигурацией.
     */
    public void loadConfig() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream input = getClass().getClassLoader()
                    .getResourceAsStream("recommendation_config.json");
            
            if (input == null) {
                throw new RuntimeException("recommendation_config.json not found in resources");
            }
            
            @SuppressWarnings("unchecked")
            Map<String, Object> parsedConfig = (Map<String, Object>) mapper.readValue(input, Map.class);
            this.config = parsedConfig;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load recommendation config", e);
        }
    }

    /**
     * Получение всей конфигурации
     * 
     * @return Map с конфигурацией рекомендаций
     */
    public Map<String, Object> getConfig() {
        return config;
    }
}

