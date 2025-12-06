package com.smartflow.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * Конфигурация Redis для кэширования
 * 
 * Настраивает Redis как провайдер кэширования для оптимизации производительности.
 * Используется для кэширования результатов поиска и рекомендаций.
 */
@Configuration
@EnableCaching
public class RedisConfig {

    /**
     * Настройка менеджера кэша Redis
     * 
     * Конфигурирует:
     * - TTL (время жизни) для кэша: 1 час
     * - Сериализацию ключей и значений
     * - Префиксы для разных типов кэша
     * 
     * @param connectionFactory фабрика подключений к Redis
     * @return настроенный менеджер кэша
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // Базовая конфигурация кэша
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1)) // TTL по умолчанию: 1 час
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues(); // Не кэшируем null значения

        // Конфигурация для кэша результатов поиска (TTL: 30 минут)
        RedisCacheConfiguration searchCacheConfig = defaultConfig.entryTtl(Duration.ofMinutes(30));

        // Конфигурация для кэша рекомендаций (TTL: 2 часа, т.к. рекомендации обновляются реже)
        RedisCacheConfiguration recommendationsCacheConfig = defaultConfig.entryTtl(Duration.ofHours(2));

        // Конфигурация для кэша треков по жанрам (TTL: 1 час)
        RedisCacheConfiguration genreCacheConfig = defaultConfig.entryTtl(Duration.ofHours(1));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration("searchResults", searchCacheConfig)
                .withCacheConfiguration("recommendations", recommendationsCacheConfig)
                .withCacheConfiguration("genreTracks", genreCacheConfig)
                .build();
    }
}

