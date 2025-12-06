package com.smartflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Главный класс приложения SmartFlow
 * 
 * @EnableCaching - включает поддержку кэширования для оптимизации производительности
 * @EnableJpaAuditing - включает автоматическое отслеживание дат создания/обновления сущностей
 */
@SpringBootApplication
@EnableCaching
@EnableJpaAuditing
public class SmartFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartFlowApplication.class, args);
    }
}

