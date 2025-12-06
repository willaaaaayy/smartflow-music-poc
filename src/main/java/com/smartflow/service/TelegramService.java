package com.smartflow.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для работы с Telegram Bot API
 * 
 * Обеспечивает отправку сообщений, обработку команд и интеграцию
 * с Telegram ботом для управления рекомендациями через чат.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramService {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${telegram.bot.token:}")
    private String botToken;

    @Value("${telegram.bot.enabled:false}")
    private boolean botEnabled;

    private static final String TELEGRAM_API_BASE_URL = "https://api.telegram.org/bot";

    /**
     * Отправка сообщения в Telegram
     * 
     * @param chatId ID чата
     * @param text текст сообщения
     * @return результат отправки
     */
    public Mono<Boolean> sendMessage(Long chatId, String text) {
        if (!botEnabled || botToken == null || botToken.isEmpty()) {
            log.warn("Telegram bot не настроен или отключен");
            return Mono.just(false);
        }

        String url = TELEGRAM_API_BASE_URL + botToken + "/sendMessage";

        Map<String, Object> payload = new HashMap<>();
        payload.put("chat_id", chatId);
        payload.put("text", text);
        payload.put("parse_mode", "HTML");

        WebClient webClient = webClientBuilder.baseUrl(url).build();

        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> {
                    boolean ok = response.path("ok").asBoolean(false);
                    if (ok) {
                        log.info("Сообщение отправлено в чат {}", chatId);
                    } else {
                        log.error("Ошибка отправки сообщения: {}", response.path("description").asText());
                    }
                    return ok;
                })
                .onErrorResume(error -> {
                    log.error("Ошибка при отправке сообщения в Telegram", error);
                    return Mono.just(false);
                });
    }

    /**
     * Отправка сообщения с клавиатурой (кнопками)
     * 
     * @param chatId ID чата
     * @param text текст сообщения
     * @param keyboard клавиатура (массив массивов строк)
     * @return результат отправки
     */
    public Mono<Boolean> sendMessageWithKeyboard(Long chatId, String text, String[][] keyboard) {
        if (!botEnabled || botToken == null || botToken.isEmpty()) {
            return Mono.just(false);
        }

        String url = TELEGRAM_API_BASE_URL + botToken + "/sendMessage";

        Map<String, Object> replyMarkup = new HashMap<>();
        replyMarkup.put("keyboard", keyboard);
        replyMarkup.put("resize_keyboard", true);
        replyMarkup.put("one_time_keyboard", false);

        Map<String, Object> payload = new HashMap<>();
        payload.put("chat_id", chatId);
        payload.put("text", text);
        payload.put("parse_mode", "HTML");
        payload.put("reply_markup", replyMarkup);

        WebClient webClient = webClientBuilder.baseUrl(url).build();

        return webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> response.path("ok").asBoolean(false))
                .onErrorResume(error -> {
                    log.error("Ошибка при отправке сообщения с клавиатурой", error);
                    return Mono.just(false);
                });
    }

    /**
     * Получение информации о боте
     * 
     * @return информация о боте
     */
    public Mono<JsonNode> getBotInfo() {
        if (!botEnabled || botToken == null || botToken.isEmpty()) {
            return Mono.empty();
        }

        String url = TELEGRAM_API_BASE_URL + botToken + "/getMe";
        WebClient webClient = webClientBuilder.baseUrl(url).build();

        return webClient.get()
                .retrieve()
                .bodyToMono(JsonNode.class)
                .onErrorResume(error -> {
                    log.error("Ошибка при получении информации о боте", error);
                    return Mono.empty();
                });
    }
}
