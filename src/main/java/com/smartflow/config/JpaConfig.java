package com.smartflow.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Конфигурация JPA
 * 
 * Включает автоматическое отслеживание дат создания/обновления сущностей
 * и управление транзакциями для работы с базой данных.
 */
@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.smartflow.repository")
@EnableTransactionManagement
public class JpaConfig {
    // Конфигурация JPA выполняется через аннотации
    // Все настройки подключения к БД находятся в application.yml
}

