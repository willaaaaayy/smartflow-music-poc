package com.smartflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Главный класс приложения SmartFlow
 * 
 * @EnableCaching - включает поддержку кэширования для оптимизации производительности
 * JPA Auditing настроен в JpaConfig
 */
@SpringBootApplication
@EnableCaching
public class SmartFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartFlowApplication.class, args);
    }
}

